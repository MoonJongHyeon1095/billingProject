package com.github.config.writer;

import com.github.domain.DailyVideoStatistic;
import com.github.domain.MonthlyVideoStatistic;
import com.github.domain.VideoStatistic;
import com.github.mapper.VideoStatisticMapper;
import com.github.util.GlobalSingletonCache;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MonthlyStatisticWriter implements ItemWriter<MonthlyVideoStatistic> {
    private final GlobalSingletonCache globalCache;

    // 생성자 주입
    @Autowired
    public MonthlyStatisticWriter() {
        this.globalCache = GlobalSingletonCache.getInstance();
    }
    @Override
    public void write(Chunk<? extends MonthlyVideoStatistic> chunk) throws Exception {
        for (MonthlyVideoStatistic stat : chunk) {

            globalCache.addMonthlyData(
                    VideoStatistic.builder()
                            .videoId(stat.getVideoId())
                            .monthlyViewCount(stat.getMonthlyViewCount())
                            .monthlyWatchedTime(stat.getMonthlyWatchedTime())
                            .build()
            ); // 데이터 전역 캐시에 추가
        }
    }
}
