package com.github.service;

import com.github.controller.response.ViewResponse;
import com.github.domain.RedisViewRecord;
import com.github.domain.Video;
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


@Slf4j
@Service
@AllArgsConstructor
public class ViewService {
    private final VideoMapper videoMapper;
    private final WatchHistoryMapper watchHistoryMapper;
    private final RedisVideoRepositoryImpl redisVideoRepository;
    private final RedisService redisService;

    /**
     1. redis에서 조회수 기록 가져오기
     2. redis에 기록이 없다면 db를 조회하여 갱신
     3. 조회수와 증가량 +1
     4. db 업데이트 조건 : 증가량 100이상
     * @param viewDto
     */
    @Transactional
    public void countView(final ViewDto viewDto){
        final int videoId = viewDto.getVideoId();
        final String lockValue = redisService.acquireRedisLock(videoId);

        try {
            // redis에서 조회수 기록 가져오기 및 업데이트
            final RedisViewRecord newRecord = findAndUpdateRedisRecord(videoId);
            // 증가량이 100마다 db에 업데이트 반영
            if (newRecord.getIncrement() % 100 ==0) {

                videoMapper.updatedTotalViewCount(
                        Video.builder()
                                .videoId(videoId)
                                .totalViewCount(newRecord.getViewCount())
                                .build()
                );

                //증가량 0으로 초기화
                //redisVideoRepository.updateHash(String.valueOf(videoId), "increment", 0);
            }
        } finally {
            redisService.releaseRedisLock(videoId, lockValue);
        }
    }

    @Transactional
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
     * 2. 없으면 db에서 조회 후 redis에 저장
     * 3. redis에 있는 조회수 기록 반환
     * @param videoId
     * @return RedisViewRecord
     */
    @Transactional
    private RedisViewRecord findAndUpdateRedisRecord(final int videoId) {
        // Redis에서 먼저 비디오 정보를 조회합니다.
        Map<String, String> currentHashMap = redisService.getRedisRecord(videoId);
        int newViewCount;
        int newIncrement;
        if (currentHashMap == null || currentHashMap.isEmpty()) {
            // Redis에 데이터가 없으면 데이터베이스에서 조회
            Video foundVideo = findVideoById(videoId);
            newViewCount = foundVideo.getTotalViewCount()+1;
            newIncrement = 1;

            // String key 패턴 설정, TTL을 설정 //24시간
            redisVideoRepository.saveHash("video:"+videoId, newViewCount, newIncrement, 86400);


        }else{
            newViewCount = Integer.parseInt(currentHashMap.get("viewCount"))+1;
            newIncrement = Integer.parseInt(currentHashMap.get("increment"))+1;

            redisVideoRepository.updateHash("video:"+videoId, "viewCount", newViewCount);
            redisVideoRepository.updateHash("video:"+videoId, "increment", newIncrement);
        }
        return RedisViewRecord.builder()
                .key("video:"+videoId)
                .viewCount(newViewCount)
                .increment(newIncrement)
                .build();
    }

    protected Video findVideoById(final int videoId){
        return videoMapper.findOneVideoById(videoId).orElseThrow(()->new VideoException(VideoErrorCode.VIDEO_NOT_FOUND));
    }

}
