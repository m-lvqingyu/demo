package com.lv.demo.biz;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Throwables;
import com.lv.demo.enums.*;
import com.lv.demo.exception.BaseException;
import com.lv.demo.pojo.UserInfo;
import com.lv.demo.req.register.AccountRgReq;
import com.lv.demo.req.register.PhoneRgReq;
import com.lv.demo.resp.auth.WeChatAccessTokenResp;
import com.lv.demo.resp.sms.ImageCircleCaptchaResp;
import com.lv.demo.service.MqService;
import com.lv.demo.service.UserInfoService;
import com.lv.demo.utils.BloomFilterUtil;
import com.lv.demo.utils.PasswordUtil;
import com.lv.demo.utils.SmsConstants;
import com.lv.demo.utils.WeChatAuthUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lv
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RegisterBiz {

    @Value("${user.worker.id}")
    private long workerId;

    @Value("${user.datacenter.id}")
    private long datacenterId;

    private final static int QUERY_MAX_LIMIT = 5000;

    private final SmsBiz smsBiz;

    private final BloomFilterUtil bloomFilterUtil;

    private final UserInfoService userInfoService;

    private final RedissonClient redissonClient;

    private final WeChatAuthUtil weChatAuthUtil;

    private final MqService mqService;

    private RBloomFilter<String> phoneBloomFilter = null;

    private RBloomFilter<String> userNameBloomFilter = null;
    /**
     * 布隆过滤器预估插入数据量
     */
    private static final int EXPECTED_INSERTIONS = 99999999;
    /**
     * 布隆过滤器误判率
     */
    private static final double FALSE_POSITIVE_PROBABILITY = 0.01D;
    /**
     * 初始化布隆过滤器分页查询，每页最大查询条数
     */
    private static final int MAX_PAGE_LIMIT = 1000;
    /**
     * 初始化手机号码布隆过滤器分布式锁名称
     */
    private static final String PHONE_BLOOM_FILTER_LOCK = "lock:bl:phone";
    /**
     * 初始化用户名布隆过滤器分布式锁名称
     */
    private static final String NAME_BLOOM_FILTER_LOCK = "lock:bl:name";

    /**
     * 初始化手机号码布隆过滤器
     */
    @PostConstruct
    public void initPhoneBloomFilter() {
        RLock lock = redissonClient.getLock(PHONE_BLOOM_FILTER_LOCK);
        try {
            if (!lock.tryLock()) {
                log.info("[PhoneBloomFilter] Initialization skipped - another instance is initializing");
                return;
            }
            log.info("[PhoneBloomFilter] Starting initialization");
            phoneBloomFilter = initBloomFilter("bl:phone",
                    "[PhoneBloomFilter]",
                    UserInfo::getPhone,
                    UserInfo::getPhone);
        } catch (Exception e) {
            log.error("[PhoneBloomFilter] Initialization failed", e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 初始化用户名的布隆过滤器
     */
    @PostConstruct
    public void initUserNameBloomFilter() {
        RLock lock = redissonClient.getLock(NAME_BLOOM_FILTER_LOCK);
        try {
            if (!lock.tryLock()) {
                log.info("[UserNameBloomFilter] Initialization skipped - another instance is initializing");
                return;
            }
            log.info("[UserNameBloomFilter] Starting initialization");
            userNameBloomFilter = initBloomFilter("bl:name",
                    "[UserNameBloomFilter]",
                    UserInfo::getUserName,
                    UserInfo::getUserName);
            log.info("[UserNameBloomFilter] Initialization completed");
        } catch (Exception e) {
            log.error("[UserNameBloomFilter] Initialization failed", e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 校验用户名是否存在
     *
     * @param userName 用户名
     */
    @SentinelResource(value = "checkNameExist",
            blockHandler = "checkNameExistHandleBlock",
            fallback = "checkNameExistHandleFallback")
    public Boolean checkNameExist(String userName) {
        if (!userNameBloomFilter.contains(userName)) {
            return true;
        }
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getUserName, userName);
        wrapper.eq(UserInfo::getDelStatus, DelStatus.NO.getKey());
        return userInfoService.getOne(wrapper) == null;
    }

    /**
     * 熔断降级处理
     *
     * @param userName 用户名
     * @param ex       异常信息
     */
    public Boolean checkNameExistHandleBlock(String userName, BlockException ex) {
        log.error("[CheckNameExist]-进入熔断降级逻辑 userName:{}， msg:{}",
                userName,
                Throwables.getStackTraceAsString(ex));
        return false;
    }

    /**
     * 业务异常降级方法
     *
     * @param userName 用户名
     * @param ex       异常信息
     */
    public Boolean checkNameExistHandleFallback(String userName, Throwable ex) {
        log.error("[CheckNameExist]-业务异常降级方法 userName:{}， msg:{}",
                userName,
                Throwables.getStackTraceAsString(ex));
        return false;
    }

    /**
     * 校验手机号码是否存在
     *
     * @param phone 手机号码
     */
    @SentinelResource(value = "checkPhoneExist",
            blockHandler = "checkPhoneExistHandleBlock",
            fallback = "checkPhoneExistHandleFallback")
    public Boolean checkPhoneExist(String phone) {
        if (phone.equalsIgnoreCase("1")) {
            throw new RuntimeException();
        }
        if (!phoneBloomFilter.contains(phone)) {
            return true;
        }
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserInfo::getPhone, phone);
        wrapper.eq(UserInfo::getDelStatus, DelStatus.NO.getKey());
        return userInfoService.getOne(wrapper) == null;
    }

    /**
     * 熔断降级处理
     *
     * @param phone 手机号码
     * @param ex    异常信息
     */
    public Boolean checkPhoneExistHandleBlock(String phone, BlockException ex) {
        log.error("[CheckPhoneExist]-进入熔断降级逻辑 phone:{}， msg:{}",
                phone,
                Throwables.getStackTraceAsString(ex));
        return false;
    }

    /**
     * 业务异常降级方法
     *
     * @param phone 手机号码
     * @param ex    异常信息
     */
    public Boolean checkPhoneExistHandleFallback(String phone, Throwable ex) {
        log.error("[CheckPhoneExist]-业务异常降级方法 phone:{}， msg:{}",
                phone,
                Throwables.getStackTraceAsString(ex));
        return false;
    }

    /**
     * 用户注册（账户密码注册）
     *
     * @param req 用户信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void accountRegister(AccountRgReq req) {
        String userName = req.getUserName();
        RLock lock = redissonClient.getLock("lk:rg:ac:" + userName);
        try {
            if (!lock.tryLock()) {
                throw new BaseException(ErrorCode.FREQUENT_OPERATION);
            }
            checkNameExist(userName);
            UserInfo userInfo = buildUserInfo();
            userInfo.setUserName(userName);
            userInfo.setEmail(req.getEmail());
            String passwordSalt = IdUtil.getSnowflakeNextIdStr();
            userInfo.setPasswordSalt(passwordSalt);
            String hashPassword = PasswordUtil.hashPassword(req.getPassword(), passwordSalt);
            userInfo.setPasswordHash(hashPassword);
            userInfo.setNickName(req.getNickName());
            userInfo.setGender(req.getGender());
            userInfo.setBirthday(DateUtil.parse(req.getBirthday(), DatePattern.NORM_DATE_PATTERN));
            if (!userInfoService.save(userInfo)) {
                throw new BaseException(ErrorCode.INTERNAL_ERROR);
            }
            userNameBloomFilter.add(userName);
            mqService.sendUserRegisterMsg(userInfo.getId());
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("[AccountRegister]-err:{}", Throwables.getStackTraceAsString(e));
            throw new BaseException(ErrorCode.INTERNAL_ERROR);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取图形验证码
     *
     */
    public ImageCircleCaptchaResp getImageCircleCaptcha() {
        String captchaId = IdUtil.getSnowflakeNextIdStr();
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(200, 100, 4, 20);
        String code = captcha.getCode();
        RBucket<String> bucket = redissonClient.getBucket(
                SmsConstants.SMS_REGISTER_IMAGE_CIRCLE_CAPTCHA_PREFIX + captchaId);
        bucket.set(code, Duration.ofMinutes(5));
        String imageBase64Data = captcha.getImageBase64Data();
        return new ImageCircleCaptchaResp(captchaId, imageBase64Data);
    }

    /**
     * 手机号码注册
     *
     * @param req 注册信息
     */
    public void phoneRegister(PhoneRgReq req) {
        RLock lock = redissonClient.getLock("lk:rg:pe:" + req.getPhone());
        try {
            if (!lock.tryLock()) {
                throw new BaseException(ErrorCode.FREQUENT_OPERATION);
            }
            RBucket<String> bucket = redissonClient.getBucket(
                    SmsConstants.SMS_REGISTER_IMAGE_CIRCLE_CAPTCHA_PREFIX + req.getCaptchaId());
            String code = bucket.get();
            if (StringUtils.isBlank(code)) {
                throw new BaseException(ErrorCode.CAPTCHA_ERROR);
            }
            if (!code.equalsIgnoreCase(req.getCode())) {
                throw new BaseException(ErrorCode.CAPTCHA_NOT_EXIST);
            }
            String phone = req.getPhone();
            checkPhoneExist(phone);
            UserInfo userInfo = buildUserInfo();
            userInfo.setPhone(phone);
            if (!userInfoService.save(userInfo)) {
                throw new BaseException(ErrorCode.INTERNAL_ERROR);
            }
            phoneBloomFilter.add(phone);
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("[PhoneRegister]-err:{}", Throwables.getStackTraceAsString(e));
            throw new BaseException(ErrorCode.INTERNAL_ERROR);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 微信注册
     *
     * @param code code
     */
    public void wechatRegister(String code) {
        RLock lock = redissonClient.getLock("lk:rg:wt:" + code);
        try {
            if (!lock.tryLock()) {
                throw new BaseException(ErrorCode.FREQUENT_OPERATION);
            }
            WeChatAccessTokenResp accessToken = weChatAuthUtil.getAccessToken(code);
            String unionId = accessToken.getUnionId();
            UserInfo userInfo = buildUserInfo();
            userInfo.setUnionId(unionId);
            if (!userInfoService.save(userInfo)) {
                throw new BaseException(ErrorCode.INTERNAL_ERROR);
            }
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("[WechatRegister]-err:{}", Throwables.getStackTraceAsString(e));
            throw new BaseException(ErrorCode.INTERNAL_ERROR);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 注册发送短信
     *
     * @param phone       手机号码
     * @param captchaId   验证码ID
     * @param captchaCode 验证码
     */
    public void registerSmsSend(String phone, String captchaId, String captchaCode) {
        RBucket<String> captchaBucket = redissonClient.getBucket(
                SmsConstants.SMS_REGISTER_IMAGE_CIRCLE_CAPTCHA_PREFIX + captchaId);
        String captchaCodes = captchaBucket.get();
        if (StringUtils.isBlank(captchaCodes)) {
            throw new BaseException(ErrorCode.CAPTCHA_ERROR);
        }
        if (!captchaCodes.equalsIgnoreCase(captchaCode)) {
            throw new BaseException(ErrorCode.CAPTCHA_NOT_EXIST);
        }
        String dayLimitKey = SmsConstants.SMS_REGISTER_DAY_LIMIT_PREFIX + phone
                + SmsConstants.KEY_SEPARATOR
                + LocalDate.now().format(DateTimeFormatter.ofPattern(DatePattern.PURE_DATE_PATTERN));
        Integer dayLimit = SmsConstants.SMS_REGISTER_DAY_LIMIT;
        String lastLimitKey = SmsConstants.SMS_REGISTER_LAST_LIMIT_PREFIX + phone;
        Long lastLimit = SmsConstants.SMS_REGISTER_LAST_LIMIT;
        String code = RandomUtil.randomNumbers(5);
        Map<String, Object> map = new HashMap<>(1);
        map.put("code", code);
        smsBiz.sendSms(dayLimitKey,
                dayLimit,
                lastLimitKey,
                lastLimit,
                SmsType.LOGIN,
                SmsPlatform.ALI,
                phone,
                map);
        RBucket<String> bucket = redissonClient.getBucket(SmsConstants.SMS_REGISTER_CODE_PREFIX + phone);
        bucket.set(code, Duration.ofMinutes(5));
    }

    private UserInfo buildUserInfo() {
        UserInfo userInfo = new UserInfo();
        Snowflake snowflake = IdUtil.getSnowflake(workerId, datacenterId);
        userInfo.setId(snowflake.nextId());
        userInfo.setStatus(UserStatus.INACTIVE.getCode());
        userInfo.setIsVerified(UserVerified.NO.getCode());
        userInfo.setVersion(0L);
        userInfo.setDelStatus(DelStatus.NO.getKey());
        userInfo.setCreateTime(new Date());
        return userInfo;
    }

    private <T> RBloomFilter<T> initBloomFilter(
            String filterKey,
            String logPrefix,
            SFunction<UserInfo, T> selectField,
            SFunction<UserInfo, T> mapField) {
        RBloomFilter<T> bloomFilter = bloomFilterUtil.getBloomFilter(
                filterKey,
                EXPECTED_INSERTIONS,
                FALSE_POSITIVE_PROBABILITY);
        if (bloomFilter.isExists()) {
            log.info("{} already exists", logPrefix);
            return bloomFilter;
        }
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(selectField)
                .isNotNull(selectField)
                .eq(UserInfo::getDelStatus, DelStatus.NO.getKey());

        int currentPage = 1;
        while (currentPage <= MAX_PAGE_LIMIT) {
            Page<UserInfo> page = new Page<>(currentPage, QUERY_MAX_LIMIT);
            Page<UserInfo> result = userInfoService.page(page, wrapper);
            if (CollectionUtils.isEmpty(result.getRecords())) {
                break;
            }
            result.getRecords().parallelStream()
                    .map(mapField)
                    .filter(s -> {
                        if (s instanceof String) {
                            return StringUtils.isNotBlank((String) s);
                        } else {
                            return s != null;
                        }
                    })
                    .forEach(bloomFilter::add);
            if (!result.hasNext()) {
                break;
            }
            currentPage++;
        }
        if (currentPage > MAX_PAGE_LIMIT) {
            log.warn("{}-Init query page exceeds limit {}", logPrefix, MAX_PAGE_LIMIT);
        }
        return bloomFilter;
    }

}
