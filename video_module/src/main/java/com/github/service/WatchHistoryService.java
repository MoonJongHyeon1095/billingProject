package com.github.service;

import com.github.domain.WatchHistory;
import com.github.dto.StopDto;
import com.github.feignclient.AdFeignClient;
import com.github.mapper.WatchHistoryMapper;
import com.github.util.DateColumnCalculator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class WatchHistoryService {
    private final DateColumnCalculator dateColumnCalculator;
    private final WatchHistoryMapper watchHistoryMapper;
    private final AdFeignClient adFeignClient;

    public void createWatchHistory(final StopDto stopDto, final String deviceUUID) {
        final DateColumnCalculator.CustomDate date = dateColumnCalculator.createDateObject();
        WatchHistory watchHistory = WatchHistory.builder()
                .videoId(stopDto.getVideoId())
                .playedTime(stopDto.getPlayedTime())
                .lastWatched(stopDto.getLastWatched())
                .adviewCount(stopDto.getAdViewCount())
                .UUID(deviceUUID)
                .year(date.getYear())
                .month(date.getMonth())
                .week(date.getWeek())
                .day(date.getDay())
                .createdAt(dateColumnCalculator.getCurrentTime())
                .build();
        watchHistory.setUserId(stopDto.getUserId());

        watchHistoryMapper.insertWatchHistory(watchHistory);

        if(watchHistory.getAdviewCount() > 0) {
            createAdDetail(watchHistory);
        }
    }

    private void createAdDetail( WatchHistory watchHistory) {
        try {
            adFeignClient.createAdDetail(watchHistory);
        } catch (Exception e){
            //TODO: Exception 만들기
            throw new RuntimeException("feign client error", e);
        }
    }

}
