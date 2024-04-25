package com.github.config.writer;

import com.github.domain.VideoStatistic;
import com.github.mapper.StatisticMapper;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MonthlyStatisticWriter implements ItemWriter<VideoStatistic> {
    private final StatisticMapper statisticMapper;

    public MonthlyStatisticWriter(StatisticMapper statisticMapper) {
        this.statisticMapper = statisticMapper;
    }

    @Override
    public void write(Chunk<? extends VideoStatistic> chunk) throws Exception {
        for(VideoStatistic videoStat : chunk){
            Optional<VideoStatistic> foundStatistic = statisticMapper.findOneByVideoId(videoStat.getVideoId());
            if(foundStatistic.isPresent()){
                statisticMapper.updateMonthlyStatistic(
                        VideoStatistic.builder()
                                .videoId(videoStat.getVideoId())
                                .monthlyViewCount(foundStatistic.get().getMonthlyViewCount() + videoStat.getMonthlyViewCount())
                                .monthlyWatchedTime(foundStatistic.get().getMonthlyWatchedTime() + videoStat.getMonthlyWatchedTime())
                                .build()
                );
            }else{
                statisticMapper.insertMonthlyStatistic(
                        VideoStatistic.builder()
                                .videoId(videoStat.getVideoId())
                                .monthlyViewCount(videoStat.getMonthlyViewCount())
                                .monthlyWatchedTime(videoStat.getMonthlyWatchedTime())
                                .build()
                );
            }
        }
    }
}
