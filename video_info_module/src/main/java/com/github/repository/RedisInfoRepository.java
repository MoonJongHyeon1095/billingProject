package com.github.repository;

import com.github.dto.PeriodViewTop5Dto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisInfoRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisInfoRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(final String key, final String value, final long ttl){
        redisTemplate.opsForValue().set(key, value);
        redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
    }

    public Optional<String> getByKey(final String key) {
        return Optional.ofNullable( redisTemplate.opsForValue().get(key));
    }
}
