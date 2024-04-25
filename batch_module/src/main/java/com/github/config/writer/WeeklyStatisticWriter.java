package com.github.config.writer;

import com.github.common.exception.GlobalException;
import com.github.domain.VideoStatistic;
import com.github.mapper.StatisticMapper;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WeeklyStatisticWriter implements ItemWriter<VideoStatistic> {
    private final StatisticMapper statisticMapper;

    public WeeklyStatisticWriter(StatisticMapper statisticMapper) {
        this.statisticMapper = statisticMapper;
    }

    @Override
    public void write(Chunk<? extends VideoStatistic> chunk) throws Exception {
        for(VideoStatistic videoStat : chunk){
            Optional<VideoStatistic> foundStatistic = statisticMapper.findOneByVideoId(videoStat.getVideoId());
            if(foundStatistic.isPresent()){
                statisticMapper.updateWeeklyStatistic(
                        VideoStatistic.builder()
                                .videoId(videoStat.getVideoId())
                                .weeklyViewCount(foundStatistic.get().getWeeklyViewCount() + videoStat.getWeeklyViewCount())
                                .weeklyWatchedTime(foundStatistic.get().getWeeklyWatchedTime() + videoStat.getWeeklyWatchedTime())
                                .build()
                        );
            }else{
                statisticMapper.insertWeeklyStatistic(
                        VideoStatistic.builder()
                                .videoId(videoStat.getVideoId())
                                .weeklyViewCount(videoStat.getWeeklyViewCount())
                                .weeklyWatchedTime(videoStat.getWeeklyWatchedTime())
                                .build()
                );
            }
        }
    }

}
