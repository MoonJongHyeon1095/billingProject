package com.github.config;

import com.github.domain.Statistic;
import com.github.domain.VideoStatistic;
import com.github.mapper.StatisticMapper;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.Map;

public class StatisticsWriter implements ItemWriter<Map<Integer, Statistic>> {
    private final StatisticMapper statisticMapper;

    public StatisticsWriter(StatisticMapper statisticMapper) {
        this.statisticMapper = statisticMapper;
    }

    @Override
    public void write(Chunk<? extends Map<Integer, Statistic>> chunk) throws Exception {
        for (Map<Integer, Statistic> statisticsMap : chunk) {
            //이중 for문 같지만, statisticsMap 하나에 stats가 하나 뿐이다.
            for (Statistic stat : statisticsMap.values()) {
                VideoStatistic videStat = VideoStatistic.builder()
                        .videoId(stat.getVideoId())
                        .dailyWatchedTime(stat.getTotalWatchTime())
                        .dailyViewCount(stat.getTotalViewCount())
                        .build();

                statisticMapper.upsertDailyStatistic(videStat);

            }
        }
    }
}
