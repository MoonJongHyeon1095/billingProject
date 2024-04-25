package com.github.config.writer;

import com.github.domain.DailyVideoStatistic;
import com.github.domain.VideoStatistic;
import com.github.util.GlobalSingletonCache;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DailyStatisticWriter implements ItemWriter<DailyVideoStatistic> {
    private final GlobalSingletonCache globalCache;

    // 생성자 주입
    @Autowired
    public DailyStatisticWriter() {
        this.globalCache = GlobalSingletonCache.getInstance();
    }
    @Override
    public void write(Chunk<? extends DailyVideoStatistic> chunk) throws Exception {
        for (DailyVideoStatistic stat : chunk) {

            globalCache.addDailyData(
                    VideoStatistic.builder()
                        .videoId(stat.getVideoId())
                        .dailyViewCount(stat.getDailyViewCount())
                        .dailyWatchedTime(stat.getDailyWatchedTime())
                        .build()
            ); // 데이터 전역 캐시에 추가
        }
    }
}