package com.github.service;

import com.github.config.RedisLockManager;
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
    private final RedisLockManager redisLockManager;

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
        final String lockKey = "viewCountLock" + videoId;
        final String lockValue = String.valueOf(System.currentTimeMillis());

        /**
         분산환경에서의 락: Redis는 일반적으로 분산 환경에서 사용되는 메모리 캐시 솔루션이므로, 여러 서버로부터 같은 값을 바꿔댈 수 있다.
         Redis의 SETNX 명령어(SET if Not eXists)와 EXPIRE 명령어를 사용하여 구현.
         먼저 SETNX 명령어를 사용하여 락을 획득하고, 이후에 EXPIRE 명령어를 사용하여 락의 만료 시간을 설정.

         lockKey는 락을 식별하는 고유한 키로 사용됩니다.
         각각의 videoId에 대해 고유한 락을 생성하기 위해 videoId를 조합하여 사용.

         lockValue는 락의 값으로, 보통 현재 시간이나 랜덤한 값 등이 사용. 이 값은 락을 해제할 때 사용.
         만약 락을 획득한 스레드가 락을 해제하지 않고 종료되는 경우를 대비하여, 일정 시간이 지나면 자동으로 락을 해제
         */
        if (!redisLockManager.acquireLock(lockKey, lockValue)) {
            throw new VideoException(VideoErrorCode.REDIS_LOCK_NOT_AVAILABLE);
        }

        try {
            // redis에서 조회수 기록 가져오기 및 업데이트
            final RedisViewRecord newRecord = findAndUpdateRedisRecord(videoId);
            // 증가량이 100이상이면 db에 업데이트 후 증가량 0으로 초기화
            if (newRecord.getIncrement() >= 100) {

                videoMapper.updateViewCount(
                        Video.builder()
                                .videoId(videoId)
                                .viewCount(newRecord.getViewCount())
                                .build()
                );

                //증가량 0으로 초기화
                redisVideoRepository.updateHash(String.valueOf(videoId), "increment", 0);
            }
        } finally {
            redisLockManager.releaseLock(lockKey, lockValue);
        }
    }

    @Transactional
    public ViewResponse getLastWatched(final ViewDto viewDto, final String deviceUUID) {
        //로그인을 하고 플레이한 경우
        if(viewDto.getUserId().isPresent()){
            System.out.println("userId:  " +viewDto.getUserId().get());
            Integer lastWatched = watchHistoryMapper.findLastWatchedByUserId(viewDto.getUserId().get()).orElse(0);
            return ViewResponse.builder().lastWatched(lastWatched).build();
        }else {
            Integer lastWatched = watchHistoryMapper.findLastWatchedByUUID(deviceUUID).orElse(0);
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
        Map<String, String> currentHashMap = redisVideoRepository.getHashMap(String.valueOf(videoId));
        int newViewCount;
        int newIncrement;
        if (currentHashMap == null || currentHashMap.isEmpty()) {
            // Redis에 데이터가 없으면 데이터베이스에서 조회
            Video foundVideo = findVideoById(videoId);
            newViewCount = foundVideo.getViewCount()+1;
            newIncrement = 1;

            // Redis에 비디오 정보를 저장하고, TTL을 설정 //3600초
            redisVideoRepository.saveHash(String.valueOf(videoId), newViewCount, newIncrement, 3600);


        }else{
            newViewCount = Integer.parseInt(currentHashMap.get("viewCount"))+1;
            newIncrement = Integer.parseInt(currentHashMap.get("increment"))+1;

            redisVideoRepository.updateHash(String.valueOf(videoId), "viewCount", newViewCount);
            redisVideoRepository.updateHash(String.valueOf(videoId), "increment", newIncrement);
        }
        return RedisViewRecord.builder()
                .videoId(String.valueOf(videoId))
                .viewCount(newViewCount)
                .increment(newIncrement)
                .build();
    }

    public void updateIfTtlExpired(final String expiredVideoId){
        Video foundVideo = findVideoById(Integer.parseInt(expiredVideoId));
        videoMapper.updateViewCount(foundVideo);
    }

    protected Video findVideoById(final int videoId){
        return videoMapper.findOneVideoById(videoId).orElseThrow(()->new VideoException(VideoErrorCode.VIDEO_NOT_FOUND));
    }


}
