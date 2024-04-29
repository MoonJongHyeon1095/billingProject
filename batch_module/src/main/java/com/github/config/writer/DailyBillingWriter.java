package com.github.config.writer;


import com.github.domain.statistic.VideoStatistic;
import com.github.mapper.VideoStatisticMapper;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DailyBillingWriter implements ItemWriter<VideoStatistic> {
    private final VideoStatisticMapper videoStatisticMapper;

    @Autowired
    public DailyBillingWriter(VideoStatisticMapper videoStatisticMapper) {
        this.videoStatisticMapper = videoStatisticMapper;
    }
    @Override
    public void write(Chunk<? extends VideoStatistic> chunk) throws Exception {
        //LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDate today = LocalDate.parse("2024-04-04");

        for (VideoStatistic stat : chunk) {
            videoStatisticMapper.updateDailyBill(stat.getVideoId(), Long.valueOf(stat.getDailyBill()), today);

        }
    }
}