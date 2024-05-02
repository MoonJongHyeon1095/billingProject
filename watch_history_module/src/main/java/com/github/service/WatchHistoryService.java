package com.github.service;

import com.github.domain.Video;
import com.github.domain.WatchHistory;
import com.github.dto.WatchHistoryDto;
import com.github.exception.VideoErrorCode;
import com.github.exception.VideoException;
import com.github.feignclient.AdFeignClient;
import com.github.mapper.VideoMapper;
import com.github.mapper.WatchHistoryMapper;
import lombok.AllArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
@AllArgsConstructor
public class WatchHistoryService {
    private final WatchHistoryMapper watchHistoryMapper;
    private final VideoMapper videoMapper;
    private final ResilienceService resilienceService;

    @Transactional
    public void createWatchHistory(final WatchHistoryDto watchHistoryDto, final String deviceUUID) {
        //video 없으면 예외처리
        validateVideo(watchHistoryDto.getVideoId());

        final String watchHistoryId = createdUniqueUUID();
        final LocalDateTime watchedAtKST = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        final LocalDate createdAtKST = LocalDate.now(ZoneId.of("Asia/Seoul"));
        final Integer numericOrderKey = findMaxNumericOrderByCreatedAt(createdAtKST);

        WatchHistory watchHistory = WatchHistory.builder()
                .watchHistoryId(watchHistoryId)
                .videoId(watchHistoryDto.getVideoId())
                .playedTime(watchHistoryDto.getPlayedTime())
                .lastWatched(watchHistoryDto.getLastWatched())
                .adviewCount(watchHistoryDto.getAdViewCount())
                .deviceUUID(deviceUUID)
                .createdAt(createdAtKST)
                .watchedAt(watchedAtKST)
                .numericOrderKey(numericOrderKey)
                .build();
        watchHistory.setEmail(watchHistoryDto.getEmail());

        insertWatchHistory(watchHistory);

        if(watchHistory.getAdviewCount() > 0) {
            createAdDetail(watchHistory);
        }
    }

    private void insertWatchHistory( final WatchHistory watchHistory){
        Integer row = watchHistoryMapper.insertWatchHistory(watchHistory);
        if(row ==0) throw new VideoException(VideoErrorCode.INSERT_WATCH_HISTORY_FAILED);
    }

    private void createAdDetail( final WatchHistory watchHistory) {
        try {
            resilienceService.createAdDetail(watchHistory.getVideoId(), watchHistory.getAdviewCount());
        } catch (Exception e){
            //TODO: Exception 만들기
            throw new RuntimeException("feign client error", e);
        }
    }

    private String createdUniqueUUID(){
        String baseUUID = UUID.randomUUID().toString();
        long currentMillis = System.currentTimeMillis();
        String watchHistoryId = baseUUID + "_" + currentMillis;
        if (watchHistoryId.length() > 50) {
            return watchHistoryId.substring(0, 50);  // 50글자 미만으로 자르기
        }
        return watchHistoryId;
    }

    @Transactional(readOnly = true)
    private Integer findMaxNumericOrderByCreatedAt(LocalDate createdAt){
        return videoMapper.findMaxNumericOrderByCreatedAt(createdAt);
    }
    @Transactional(readOnly = true)
    private void validateVideo(final int videoId){
        videoMapper.findOneVideoById(videoId).orElseThrow(()->new VideoException(VideoErrorCode.VIDEO_NOT_FOUND));
    }

}