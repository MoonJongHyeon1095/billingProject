package com.github.config.processor;

import com.github.domain.VideoStatistic;
import com.github.domain.WatchHistory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class MonthlyStatisticsProcessor  implements ItemProcessor<WatchHistory, VideoStatistic> {
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
                        .monthlyWatchedTime(item.getPlayedTime())
                        .monthlyViewCount(1)
                        .build();
            } else {
                // 캐시에 있으면 누적
                videoStatistic.setMonthlyWatchedTime(videoStatistic.getMonthlyWatchedTime() + item.getPlayedTime());
                videoStatistic.setMonthlyViewCount(videoStatistic.getMonthlyViewCount() + 1);
                return videoStatistic;
            }
        });

        return videoStatisticsCache.get(videoId);
    }

    public void clearCache() {
        videoStatisticsCache.clear(); // 캐시 비우기
    }
}
