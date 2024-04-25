package com.github.config.writer;

import com.github.domain.VideoStatistic;
import com.github.mapper.StatisticMapper;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
public class DailyStatisticWriter implements ItemWriter<VideoStatistic> {
    private final StatisticMapper statisticMapper;

    public DailyStatisticWriter(StatisticMapper statisticMapper) {
        this.statisticMapper = statisticMapper;
    }

    @Override
    public void write(Chunk<? extends VideoStatistic> chunk) throws Exception {
        for(VideoStatistic videoStat : chunk){
            final VideoStatistic newStat = VideoStatistic.builder()
                    .videoId(videoStat.getVideoId())
                    .dailyViewCount(videoStat.getDailyViewCount())
                    .dailyWatchedTime(videoStat.getDailyWatchedTime())
                    .build();
            if(statisticMapper.existsVideoStatisticByVideoId(newStat.getVideoId())){
                statisticMapper.updateDailyStatistic(newStat);
            }else{
                statisticMapper.insertDailyStatistic(newStat);
            }
        }
    }

}