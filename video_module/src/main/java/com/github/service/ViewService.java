package com.github.service;

import com.github.domain.RedisViewRecord;
import com.github.domain.Video;
import com.github.dto.ViewDto;
import com.github.exception.VideoErrorCode;
import com.github.exception.VideoException;
import com.github.mapper.VideoMapper;
import com.github.repository.RedisVideoRepository;
import com.github.repository.RedisVideoRepositoryImpl;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class ViewService {
    private final VideoMapper videoMapper;
    private final RedisVideoRepositoryImpl redisVideoRepository;

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
        // redis에서 조회수 기록 가져오기
        final RedisViewRecord currentRecord = findAndUpdateRedisRecord(videoId);
        // 조회수 +1, 증가량 +1
        final RedisViewRecord newRecord = increaseViewCount(currentRecord);
        // 증가량이 100이상이면 db에 업데이트 후 증가량 0으로 초기화
        if(newRecord.getDeltaView() >=100) {

            videoMapper.updateViewCount(
                    Video.builder()
                            .videoId(videoId)
                            .viewCount(newRecord.getViewCount())
                            .build()
            );

            //증가량 0으로 초기화
            redisVideoRepository.updateHash(String.valueOf(videoId), "deltaViewCount", 0);
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
        Map<String, Integer> currentHashMap = redisVideoRepository.getHashMap(String.valueOf(videoId));

        if (currentHashMap == null || currentHashMap.isEmpty()) {
            // Redis에 데이터가 없으면 데이터베이스에서 조회
            Video foundVideo = findVideoById(videoId);

            // Redis에 비디오 정보를 저장하고, TTL을 설정 //3600초
            redisVideoRepository.saveHash(String.valueOf(videoId), foundVideo.getViewCount(), 0, 3600);

            currentHashMap = new HashMap<>();
            currentHashMap.put("viewCount", foundVideo.getViewCount());
            currentHashMap.put("deltaViewCount", 0);

        }
        return RedisViewRecord.builder()
                .videoId(String.valueOf(videoId))
                .viewCount(currentHashMap.get("viewCount"))
                .deltaView(currentHashMap.get("deltaViewCount"))
                .build();
    }


    private RedisViewRecord increaseViewCount(final RedisViewRecord currentRecord){
        return RedisViewRecord.builder()
                .videoId(currentRecord.getVideoId())
                .viewCount(currentRecord.getViewCount()+1)
                .deltaView(currentRecord.getDeltaView()+1)
                .build();
    }

    @Transactional
    protected Video findVideoById(final int videoId){
        return videoMapper.findOneVideoById(videoId).orElseThrow(()->new VideoException(VideoErrorCode.VIDEO_NOT_FOUND));
    }

}
