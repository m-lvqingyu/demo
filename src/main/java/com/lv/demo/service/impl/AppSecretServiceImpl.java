package com.lv.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lv.demo.enums.AppSecretStatus;
import com.lv.demo.enums.ErrorCode;
import com.lv.demo.exception.BaseException;
import com.lv.demo.mapper.AppSecretMapper;
import com.lv.demo.pojo.AppSecret;
import com.lv.demo.service.AppSecretService;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author 18891
 * @description 针对表【app_secret(应用密钥表)】的数据库操作Service实现
 * @createDate 2025-12-23 15:18:25
 */
@Service
public class AppSecretServiceImpl extends ServiceImpl<AppSecretMapper, AppSecret>
        implements AppSecretService {

    private final RedissonClient redissonClient;

    public AppSecretServiceImpl(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    private static final String APP_SECRET_CACHE_PREFIX = "app:secret:";

    @Override
    public String getByCache(String appId) {
        String cacheKey = APP_SECRET_CACHE_PREFIX + appId;
        RBucket<AppSecret> bucket = redissonClient.getBucket(cacheKey);
        if (bucket.isExists()) {
            AppSecret appSecret = bucket.get();
            if (appSecret != null) {
                Integer status = appSecret.getStatus();
                if (!AppSecretStatus.EFFECTIVE.getKey().equals(status)) {
                    throw new BaseException(ErrorCode.APP_SECRET_INVALID);
                }
                Date expireTime = appSecret.getExpireTime();
                if (System.currentTimeMillis() > expireTime.getTime()) {
                    throw new BaseException(ErrorCode.APP_SECRET_INVALID);
                }
                return appSecret.getAppSecret();
            }
        }
        LambdaQueryWrapper<AppSecret> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AppSecret::getAppId, appId);
        wrapper.eq(AppSecret::getStatus, AppSecretStatus.EFFECTIVE.getKey());
        AppSecret appSecret = this.getOne(wrapper);
        if (appSecret == null) {
            throw new BaseException(ErrorCode.NOT_FOUND);
        }
        Date expireTime = appSecret.getExpireTime();
        if (System.currentTimeMillis() > expireTime.getTime()) {
            throw new BaseException(ErrorCode.APP_SECRET_INVALID);
        }
        bucket.set(appSecret);
        return appSecret.getAppSecret();
    }
    
}




