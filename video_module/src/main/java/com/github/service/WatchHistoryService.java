package com.github.service;

import com.github.domain.Video;
import com.github.domain.WatchHistory;
import com.github.dto.WatchHistoryDto;
import com.github.exception.VideoErrorCode;
import com.github.exception.VideoException;
import com.github.feignclient.AdFeignClient;
import com.github.mapper.WatchHistoryMapper;
import com.github.util.DateColumnCalculator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class WatchHistoryService {
    private final DateColumnCalculator dateColumnCalculator;
    private final WatchHistoryMapper watchHistoryMapper;
    private final AdFeignClient adFeignClient;
    private final ViewService viewService;

    @Transactional
    public void createWatchHistory(final WatchHistoryDto watchHistoryDto, final String deviceUUID) {
        final DateColumnCalculator.CustomDate date = dateColumnCalculator.createDateObject();
        Video video = viewService.findVideoById(watchHistoryDto.getVideoId());
        WatchHistory watchHistory = WatchHistory.builder()
                .videoId(video.getVideoId())
                .playedTime(watchHistoryDto.getPlayedTime())
                .lastWatched(watchHistoryDto.getLastWatched())
                .adviewCount(watchHistoryDto.getAdViewCount())
                .UUID(deviceUUID)
                .year(date.getYear())
                .month(date.getMonth())
                .week(date.getWeek())
                .day(date.getDay())
                .createdAt(dateColumnCalculator.getCurrentTime())
                .build();
        watchHistory.setUserId(watchHistoryDto.getUserId());

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

}
