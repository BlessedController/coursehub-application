package com.coursehub.course_service.util;


import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

import static lombok.AccessLevel.PRIVATE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RedisUtil {
    RedissonClient redissonClient;

    public <T> void saveToCache(String key, T value, Long expireTime, TemporalUnit temporalUnit) {

        RBucket<T> bucket = redissonClient.getBucket(key);

        bucket.set(value);

        bucket.expire(Duration.of(expireTime, temporalUnit));
    }

    public <T> T getDataFromCache(String cacheKey) {

        RBucket<T> bucket = redissonClient.getBucket(cacheKey);

        return bucket == null ? null : bucket.get();
    }

}
