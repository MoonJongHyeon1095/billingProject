package com.github.config.processor;

import java.util.concurrent.ConcurrentHashMap;

import com.github.domain.VideoStatistic;
import org.springframework.batch.item.ItemProcessor;
import com.github.domain.WatchHistory;
import org.springframework.stereotype.Component;

@Component
public class DailyStatisticsProcessor implements ItemProcessor<WatchHistory, VideoStatistic> {
    // 동시성 문제를 방지하기 위해 ConcurrentHashMap 사용
    private ConcurrentHashMap<Integer, VideoStatistic> videoStatisticsCache = new ConcurrentHashMap<>();
    @Override
    public VideoStatistic process(WatchHistory item) throws Exception {
        Integer videoId = item.getVideoId();

        // 캐시에서 VideoStatistic 가져오기
        videoStatisticsCache.compute(videoId, (key, videoStatistic) -> {
            if (videoStatistic == null) {
                // 캐시에 없으면 새로 생성
                return VideoStatistic.builder()
                        .videoId(videoId)
                        .dailyWatchedTime(item.getPlayedTime())
                        .dailyViewCount(1)
                        .build();
            } else {
                // 캐시에 있으면 누적
                videoStatistic.setDailyWatchedTime(videoStatistic.getDailyWatchedTime() + item.getPlayedTime());
                videoStatistic.setDailyViewCount(videoStatistic.getDailyViewCount() + 1);
                return videoStatistic;
            }
        });

        return videoStatisticsCache.get(videoId);
    }

    public void clearCache() {
        videoStatisticsCache.clear(); // 캐시 비우기
    }
}

