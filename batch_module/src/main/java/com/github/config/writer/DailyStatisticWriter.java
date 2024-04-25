package com.github.config.writer;

import com.github.domain.VideoStatistic;
import com.github.mapper.StatisticMapper;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DailyStatisticWriter implements ItemWriter<VideoStatistic> {
    private final StatisticMapper statisticMapper;

    public DailyStatisticWriter(StatisticMapper statisticMapper) {
        this.statisticMapper = statisticMapper;
    }

    @Override
    public void write(Chunk<? extends VideoStatistic> chunk) throws Exception {
        for(VideoStatistic videoStat : chunk){
            Optional<VideoStatistic> foundStatistic = statisticMapper.findOneByVideoId(videoStat.getVideoId());
            if(foundStatistic.isPresent()){
                statisticMapper.updateDailyStatistic(
                        VideoStatistic.builder()
                                .videoId(videoStat.getVideoId())
                                .dailyViewCount(foundStatistic.get().getDailyViewCount() + videoStat.getDailyViewCount())
                                .dailyWatchedTime(foundStatistic.get().getDailyWatchedTime() + videoStat.getDailyWatchedTime())
                                .build()
                );
            }else{
                statisticMapper.insertDailyStatistic(
                        VideoStatistic.builder()
                                .videoId(videoStat.getVideoId())
                                .dailyViewCount(videoStat.getDailyViewCount())
                                .dailyWatchedTime(videoStat.getDailyWatchedTime())
                                .build()
                );
            }
        }
    }

}