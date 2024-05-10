package com.github.service;

import com.github.config.redis.RedisLockManager;
import com.github.exception.VideoErrorCode;
import com.github.exception.VideoException;
import com.github.repository.RedisVideoRepositoryImpl;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class RedisService {
    private final RedisLockManager redisLockManager;
    private final RedisVideoRepositoryImpl redisVideoRepository;

    public RedisService(RedisLockManager redisLockManager, RedisVideoRepositoryImpl redisVideoRepository) {
        this.redisLockManager = redisLockManager;
        this.redisVideoRepository = redisVideoRepository;
    }

    /**
     * Redis 락을 획득합니다.
     *
     * @param videoId
     * @return lockValue
     * @throws VideoException
     */
    protected String acquireRedisLock(final int videoId) {
        final String lockKey = "viewCountLock" + videoId;
        final String lockValue = String.valueOf(System.currentTimeMillis());

        boolean isLockAcquired = redisLockManager.acquireLock(lockKey, lockValue);

        if (!isLockAcquired) {
            throw new VideoException(VideoErrorCode.REDIS_LOCK_NOT_AVAILABLE);
        }

        return lockValue;
    }

    /**
     * Redis 락을 해제합니다.
     *
     * @param videoId
     * @param lockValue
     */
    protected void releaseRedisLock(final int videoId, final String lockValue) {
        final String lockKey = "viewCountLock" + videoId;
        redisLockManager.releaseLock(lockKey, lockValue);
    }

    protected Map<String, String> getRedisRecord(final int videoId) {
        return redisVideoRepository.getHashMap("video:"+videoId);
    }

    protected Integer getViewCountValue(final int videoId){
        return redisVideoRepository.getFromHashMap("video:"+videoId, "viewCount");
    }

    private static int getVideoIdFromKey(String key) {
        if (key == null || !key.startsWith("video:")) {
            throw new VideoException(VideoErrorCode.INVALID_REDIS_KEY);
        }

        // 'video:' 이후의 부분만 가져오기
        String videoIdStr = key.substring(6); // 'video:'가 6글자이므로, 그 이후의 부분만 추출
        return Integer.parseInt(videoIdStr); // 숫자로 변환
    }

}
