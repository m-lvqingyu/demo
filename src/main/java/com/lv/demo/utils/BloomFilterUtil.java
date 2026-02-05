package com.lv.demo.utils;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * @author lv
 */
@RequiredArgsConstructor
@Component
public class BloomFilterUtil {

    private final RedissonClient redissonClient;

    /**
     * @param bloomName          过滤器名称
     * @param expectedInsertions 预测插入数量
     * @param falseProbability   误判率
     * @return 布隆过滤器
     */
    public <T> RBloomFilter<T> getBloomFilter(String bloomName,
                                              long expectedInsertions,
                                              double falseProbability) {
        RBloomFilter<T> bloomFilter = redissonClient.getBloomFilter(bloomName);
        bloomFilter.tryInit(expectedInsertions, falseProbability);
        return bloomFilter;
    }
    
}
