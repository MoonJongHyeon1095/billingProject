package com.github.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.batch.item.ItemProcessor;
import com.github.domain.WatchHistory;
import com.github.domain.Statistic;

public class StatisticsProcessor implements ItemProcessor<WatchHistory,  Map<Integer, Statistic>> {
    private final Map<Integer, Statistic> statisticsMap = new HashMap<>();

    @Override
    public  Map<Integer, Statistic> process(WatchHistory watchHistory) {
        int videoId = watchHistory.getVideoId();
        int playedTime = watchHistory.getPlayedTime();

        // 만약 이 videoId에 대한 통계가 아직 없으면 새로 생성
        if (!statisticsMap.containsKey(videoId)) {
            statisticsMap.put(videoId, Statistic.builder()
                    .videoId(videoId)
                    .totalWatchTime(0L)
                    .totalViewCount(0L)
                    .build());
        }

        // 해당 videoId에 대한 총 시청 시간을 업데이트
        Statistic stats = statisticsMap.get(videoId);
        stats.setTotalWatchTime(stats.getTotalWatchTime() + playedTime);
        stats.setTotalViewCount(stats.getTotalViewCount() + 1);

        return statisticsMap;
    }
}

