package com.lv.demo.config.redis;

import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author lv
 */
@Slf4j
@Component
public class RedissonHealthCheck {

    private final RedissonClient redissonClient;

    public RedissonHealthCheck(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void checkRedissonConnection() {
        try {
            // 测试连接
            redissonClient.getKeys().count();
            log.info("[Redisson]-connects successfully");
        } catch (Exception e) {
            log.info("[Redisson]-connects fail. msg:{}", Throwables.getStackTraceAsString(e));
        }
    }
}
