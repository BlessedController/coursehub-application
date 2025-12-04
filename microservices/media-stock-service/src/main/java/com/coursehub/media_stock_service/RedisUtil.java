package com.coursehub.media_stock_service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedissonClient redissonClient;

    public <T> void saveToCache(String key, T value, Long expireTime, TemporalUnit temporalUnit) {

        RBucket<T> bucket = redissonClient.getBucket(key);

        bucket.set(value, Duration.of(expireTime, temporalUnit));
    }

    public <T> T getDataFromCache(String cacheKey) {

        RBucket<T> bucket = redissonClient.getBucket(cacheKey);

        return bucket.get();
    }

}
