package com.github.service;

import com.github.domain.Video;
import com.github.domain.WatchHistory;
import com.github.dto.WatchHistoryDto;
import com.github.exception.VideoErrorCode;
import com.github.exception.VideoException;
import com.github.feignclient.AdFeignClient;
import com.github.mapper.WatchHistoryMapper;
import lombok.AllArgsConstructor;
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
    private final AdFeignClient adFeignClient;
    private final ViewService viewService;

    @Transactional
    public void createWatchHistory(final WatchHistoryDto watchHistoryDto, final String deviceUUID) {
        Video video = viewService.findVideoById(watchHistoryDto.getVideoId());
        String watchHistoryId = createUniqueId();
        LocalDateTime watchedAtKST = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDate createdAtKST = LocalDate.now(ZoneId.of("Asia/Seoul"));
        WatchHistory watchHistory = WatchHistory.builder()
                .watchHistoryId(watchHistoryId)
                .videoId(video.getVideoId())
                .playedTime(watchHistoryDto.getPlayedTime())
                .lastWatched(watchHistoryDto.getLastWatched())
                .adviewCount(watchHistoryDto.getAdViewCount())
                .deviceUUID(deviceUUID)
                .createdAt(createdAtKST)
                .watchedAt(watchedAtKST)
                .build();
        watchHistory.setEmail(watchHistoryDto.getEmail());

        insertWatchHistory(watchHistory);

        if(watchHistory.getAdviewCount() > 0) {
            createAdDetail(watchHistory);
        }
    }

    private void insertWatchHistory( final WatchHistory watchHistory){
        Integer resultPK = watchHistoryMapper.insertWatchHistory(watchHistory);
        if(resultPK ==null) throw new VideoException(VideoErrorCode.INSERT_WATCH_HISTORY_FAILED);
    }

    private void createAdDetail( final WatchHistory watchHistory) {
        try {
            adFeignClient.createAdDetail(watchHistory.getVideoId(), watchHistory.getAdviewCount());
        } catch (Exception e){
            //TODO: Exception 만들기
            throw new RuntimeException("feign client error", e);
        }
    }

    private String createUniqueId(){
        String baseUUID = UUID.randomUUID().toString();
        long currentMillis = System.currentTimeMillis();
        String watchHistoryId = baseUUID + "_" + currentMillis;
        if (watchHistoryId.length() > 50) {
            return watchHistoryId.substring(0, 50);  // 50글자 미만으로 자르기
        }
        return watchHistoryId;
    }

}
