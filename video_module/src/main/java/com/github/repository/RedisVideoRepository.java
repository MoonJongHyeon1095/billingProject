package com.github.repository;

import java.util.Map;

public interface RedisVideoRepository {

    void saveHash(String videoId, int viewCount, int deltaViewCount, long ttl);

    Integer getFromHashMap(String videoId, String key);

    void updateHash(String videoId, String fieldKey, int fieldValue);

    void deleteHash(String key);

    Map<String, Integer> getHashMap(String videoId);
}