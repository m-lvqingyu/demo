package com.lv.demo.biz;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.base.Throwables;
import com.lv.demo.enums.*;
import com.lv.demo.exception.BaseException;
import com.lv.demo.pojo.SmsSendLog;
import com.lv.demo.pojo.SmsTemplate;
import com.lv.demo.resp.sms.SendSmsResp;
import com.lv.demo.service.SmsSendLogService;
import com.lv.demo.service.SmsTemplateService;
import com.lv.demo.utils.AliSmsUtil;
import com.lv.demo.utils.SmsConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

/**
 * @author lv
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class SmsBiz {

    private final SmsTemplateService smsTemplateService;

    private final SmsSendLogService smsSendLogService;

    private final AliSmsUtil aliSmsUtil;

    private final RedissonClient redissonClient;

    private static final String SMS_SEND_LOCK_KEY = "sms:sd:lk:";

    /**
     * 短信发送
     *
     * @param dayLimitKey  每日发送次数限制key
     * @param dayLimit     每日发送次数限制次数
     * @param lastLimitKey 检查发送频率限制key
     * @param lastLimit    检查发送频率限制(单位：毫秒)
     * @param smsType      短信类型
     * @param smsPlatform  发送平台
     * @param phone        手机号
     * @param map          短信参数
     */
    public void sendSms(
            String dayLimitKey,
            Integer dayLimit,
            String lastLimitKey,
            Long lastLimit,
            SmsType smsType,
            SmsPlatform smsPlatform,
            String phone,
            Map<String, Object> map) {
        if (StringUtils.isBlank(phone) || CollectionUtil.isEmpty(map)) {
            throw new BaseException(ErrorCode.BAD_REQUEST);
        }
        RLock lock = redissonClient.getLock(SMS_SEND_LOCK_KEY + phone);
        try {
            boolean isLock = lock.tryLock();
            if (!isLock) {
                throw new BaseException(ErrorCode.FREQUENT_OPERATION);
            }
            // 检查每日发送次数限制
            RBucket<Integer> daySendNumBucket = redissonClient.getBucket(dayLimitKey);
            Integer daySendNum = daySendNumBucket.get();
            if (daySendNum != null && daySendNum >= dayLimit) {
                throw new BaseException(ErrorCode.SMS_DAY_LIMIT);
            }
            // 检查发送频率限制
            RBucket<Long> lastSendTimeBucket = redissonClient.getBucket(lastLimitKey);
            Long lastTime = lastSendTimeBucket.get();
            long currentTime = System.currentTimeMillis();
            if (lastTime != null && lastTime > 0 && (currentTime - lastTime) < lastLimit) {
                throw new BaseException(ErrorCode.SMS_FREQUENCY_LIMIT);
            }
            // 发送短信
            SmsTemplate template = getSmsTemplates(smsType, smsPlatform);
            if (template == null) {
                throw new BaseException(ErrorCode.NOT_FOUND);
            }
            SmsSendLog smsSendLog = new SmsSendLog();
            smsSendLog.setId(IdUtil.getSnowflakeNextId());
            smsSendLog.setPhone(phone);
            smsSendLog.setTempId(template.getId());
            Date date = new Date();
            smsSendLog.setCreateTime(date);
            smsSendLog.setUpdateTime(date);
            if (!smsSendLogService.save(smsSendLog)) {
                throw new BaseException(ErrorCode.INTERNAL_ERROR);
            }
            SendSmsResp resp = aliSmsUtil.doSend(phone,
                    template.getSignName(),
                    template.getTemplateCode(),
                    JSONUtil.toJsonStr(map));
            smsSendLog.setResult(JSONUtil.toJsonStr(resp));
            smsSendLog.setStatus(resp.getSuccess());
            smsSendLog.setUpdateTime(new Date());
            boolean updated = smsSendLogService.updateById(smsSendLog);
            if (!updated || SendSmsStatus.FAILED.getKey() == resp.getSuccess()) {
                throw new BaseException(ErrorCode.INTERNAL_ERROR);
            }
            // 更新发送记录缓存
            if (daySendNum == null) {
                daySendNumBucket.set(1, Duration.ofHours(SmsConstants.SEND_TIMES_CACHE_HOURS));
            } else {
                daySendNumBucket.set(daySendNum + 1);
            }
            lastSendTimeBucket.set(System.currentTimeMillis(), Duration.ofMillis(lastLimit));
        } catch (Exception e) {
            log.error("[SMS-Send] Error:{}", Throwables.getStackTraceAsString(e));
            throw new BaseException(ErrorCode.INTERNAL_ERROR);
        } finally {
            lock.unlock();
        }
    }

    public SmsTemplate getSmsTemplates(SmsType smsType, SmsPlatform smsPlatform) {
        LambdaQueryWrapper<SmsTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SmsTemplate::getType, smsType.getKey());
        wrapper.eq(SmsTemplate::getPlatform, smsPlatform.getKey());
        wrapper.eq(SmsTemplate::getStatus, SmsTemplateStatus.EFFECTIVE.getKey());
        wrapper.eq(SmsTemplate::getDelStatus, DelStatus.NO.getKey());
        return smsTemplateService.getOne(wrapper);
    }

}
