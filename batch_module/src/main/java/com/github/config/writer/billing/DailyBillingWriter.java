package com.github.config.writer.billing;


import com.github.domain.statistic.VideoStatistic;
import com.github.mapper.VideoStatisticMapper;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DailyBillingWriter implements ItemWriter<VideoStatistic> {
    private final VideoStatisticMapper videoStatisticMapper;

    @Autowired
    public DailyBillingWriter(VideoStatisticMapper videoStatisticMapper) {
        this.videoStatisticMapper = videoStatisticMapper;
    }
    @Override
    public void write(Chunk<? extends VideoStatistic> chunk) throws Exception {
        for (VideoStatistic stat : chunk) {
            videoStatisticMapper.updateDailyBill(stat.getVideoId(), stat.getDailyBill());

        }
    }
}