package com.github.repository;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 RedisTemplate 초기화: config에서 RedisTemplate 객체를 생성하고, Redis 서버 설정과 함께 적절한 직렬화 방법을 구성. HashOperations객체 초기화.
 HashOperations : HashMap과 유사한 방식으로 작동

 */
@Repository
public class RedisVideoRepositoryImpl implements RedisVideoRepository {
    //key는 String으로 저장
    private final RedisTemplate<String, String> redisTemplate;
    private final HashOperations<String, String, String> hashOperations;

    public RedisVideoRepositoryImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }


    @Override
    public void saveHash(final String videoId, final int viewCount, final int increment, final long ttl) {
        hashOperations.put(videoId, "viewCount", String.valueOf(viewCount)); // 문자열로 변환
        hashOperations.put(videoId, "increment", String.valueOf(increment)); // 문자열로 변환
        //K key, final long timeout, final TimeUnit unit
        redisTemplate.expire(videoId, ttl, TimeUnit.SECONDS);
    }

    @Override
    public Integer getFromHashMap(final String videoId, final String key) {
        return Integer.parseInt(hashOperations.get(videoId, key));
    }

    @Override
    public void updateHash(String videoId, final String fieldKey, final int fieldValue) {
        hashOperations.put(videoId, fieldKey, String.valueOf(fieldValue));
    }

    @Override
    public void deleteHash(String videoId) {
        hashOperations.delete(videoId);
    }

    @Override
    public Map<String, String> getHashMap(String videoId) {
        return hashOperations.entries(videoId);
    }

}
