package com.github.service;

import com.github.config.executor.ExecutorServiceConfig;
import com.github.controller.response.ViewResponse;
import com.github.dto.ViewDto;
import com.github.exception.VideoErrorCode;
import com.github.exception.VideoException;
import com.github.mapper.VideoMapper;
import com.github.mapper.WatchHistoryMapper;
import com.github.repository.RedisVideoRepositoryImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Service
@AllArgsConstructor
public class ViewService {
    private final VideoMapper videoMapper;
    private final WatchHistoryMapper watchHistoryMapper;
    private final RedisVideoRepositoryImpl redisVideoRepository;
    private final RedisService redisService;
    private final ExecutorServiceConfig executorServiceConfig;

    /**
     1. DB 조회수 갱신 (비관적 락)
     2. redis 조회수 기록 갱신
     * @param viewDto
     */
    public void countView(final ViewDto viewDto){
        final int videoId = viewDto.getVideoId();

        CompletableFuture<Integer> newViewCount = CompletableFuture.supplyAsync(
                () -> findViewCountAndIncrease(videoId), executorServiceConfig.virtualThreadExecutor());

        newViewCount.thenAccept(result -> {
            final String lockValue = redisService.acquireRedisLock(videoId);
            try {
                findAndUpdateRedisRecord(videoId, result);
            } finally {
                redisService.releaseRedisLock(videoId, lockValue);
            }
        }).exceptionally(ex -> {
            throw new VideoException(VideoErrorCode.UPDATE_VIEW_COUNT_FAILED);
        });

    }

    @Transactional(readOnly = true)
    public ViewResponse getLastWatched(final ViewDto viewDto, final String deviceUUID) {
        //로그인을 하고 플레이한 경우
        if(viewDto.getEmail().isPresent()){
            Integer lastWatched = watchHistoryMapper.findLastWatchedByEmail(viewDto.getEmail().get()).orElse(0);
            return ViewResponse.builder().lastWatched(lastWatched).build();
        }else {
            Integer lastWatched = watchHistoryMapper.findLastWatchedByDeviceUUID(deviceUUID).orElse(0);
            return ViewResponse.builder().lastWatched(lastWatched).build();
        }
    }

    /**
     * 1. redis에서 조회수 기록 조회
     * 2. 없으면 바로 redis에 저장 (새로운 ttl)
     * 3. 있으면 더 큰 값으로 redis 갱신
     * @param videoId
     * @return RedisViewRecord
     */
    private void findAndUpdateRedisRecord(final int videoId, int newViewCount) {
        // Redis에서 먼저 비디오 정보를 조회합니다.
        Map<String, String> currentHashMap = redisService.getRedisRecord(videoId);

        if (currentHashMap == null || currentHashMap.isEmpty()) {
            // String key 패턴 설정, TTL을 설정 //1시간
            redisVideoRepository.saveHash("video:"+videoId, newViewCount, 3600);
        }else{
            Integer redisCount = redisService.getViewCountValue(videoId);
            newViewCount = Math.max(redisCount, newViewCount);

            redisVideoRepository.updateHash("video:"+videoId, "viewCount", newViewCount);
        }
    }
    @Transactional
    private int findViewCountAndIncrease(final int videoId){
        final Integer viewCount = videoMapper.findViewCountByVideoIdForUpdate(videoId).orElseThrow(()->new VideoException(VideoErrorCode.VIDEO_NOT_FOUND));
        final int newViewCount = viewCount+1;
        videoMapper.updatedTotalViewCountPlusOne(newViewCount, videoId);
        return newViewCount;
    }

}
