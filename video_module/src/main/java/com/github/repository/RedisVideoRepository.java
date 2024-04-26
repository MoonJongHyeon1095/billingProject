package com.github.repository;

import java.util.Map;
import java.util.Set;

public interface RedisVideoRepository {

    Set<String> getAllVideoRecords();

    void saveHash(String key, int viewCount, int deltaViewCount, long ttl);

    Integer getFromHashMap(String videoId, String key);

    void updateHash(String videoId, String fieldKey, int fieldValue);

    void deleteHash(String key);

    Map<String, String> getHashMap(String videoId);
}