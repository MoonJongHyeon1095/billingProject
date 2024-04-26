package com.github.service;

import com.github.config.redis.RedisLockManager;
import com.github.domain.RedisViewRecord;
import com.github.domain.Video;
import com.github.exception.VideoErrorCode;
import com.github.exception.VideoException;
import com.github.mapper.VideoMapper;
import com.github.repository.RedisVideoRepositoryImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
public class RedisService {
    private final RedisLockManager redisLockManager;
    private final RedisVideoRepositoryImpl redisVideoRepository;
    private final VideoMapper videoMapper;

    public RedisService(RedisLockManager redisLockManager, RedisVideoRepositoryImpl redisVideoRepository, VideoMapper videoMapper) {
        this.redisLockManager = redisLockManager;
        this.redisVideoRepository = redisVideoRepository;
        this.videoMapper = videoMapper;
    }

    // 매일 0시와 12시에 실행
    @Scheduled(cron = "0 0 0,12 * * ?", zone = "Asia/Seoul")
    @Transactional
    private void updateRecords(){
        // Redis 내의 모든 키 가져오기
        final Set<String> keys = redisVideoRepository.getAllVideoRecords(); // 키 패턴에 따라 적절히 조정

        if (keys != null) {
            for (String key : keys) {
                final Map<String, String> videoData = redisVideoRepository.getHashMap(key);
                if (videoData != null && !videoData.isEmpty()) {
                    final int viewCount = Integer.parseInt(videoData.get("viewCount"));
                    final int increment =  Integer.parseInt(videoData.get("increment"));

                    videoMapper.updatedTotalViewCount(
                            Video.builder()
                                    .videoId(getVideoIdFromKey(key))
                                    .totalViewCount(viewCount)
                            .build());

                    redisVideoRepository.saveHash(key, viewCount, increment, 86400);
                }
            }
        }
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

    private static int getVideoIdFromKey(String key) {
        if (key == null || !key.startsWith("video:")) {
            throw new VideoException(VideoErrorCode.INVALID_REDIS_KEY);
        }

        // 'video:' 이후의 부분만 가져오기
        String videoIdStr = key.substring(6); // 'video:'가 6글자이므로, 그 이후의 부분만 추출
        return Integer.parseInt(videoIdStr); // 숫자로 변환
    }

}
