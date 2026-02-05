package com.lv.demo.config.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author lv
 */
@Configuration
public class RedissonConfig {

    @Bean(name = "redissonClient")
    public RedissonClient redissonClient() throws IOException {
        // 从 classpath 加载配置文件
        InputStream configStream = getClass().getClassLoader().getResourceAsStream("redisson-dev.yml");
        Config config = Config.fromYAML(configStream);
        return Redisson.create(config);
    }
}
