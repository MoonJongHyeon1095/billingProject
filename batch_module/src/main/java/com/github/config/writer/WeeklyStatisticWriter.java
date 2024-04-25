package com.github.config.writer;

import com.github.domain.MonthlyVideoStatistic;
import com.github.domain.VideoStatistic;
import com.github.domain.WeeklyVideoStatistic;
import com.github.mapper.VideoStatisticMapper;
import com.github.util.GlobalSingletonCache;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WeeklyStatisticWriter implements ItemWriter<WeeklyVideoStatistic> {
    private final GlobalSingletonCache globalCache;

    // 생성자 주입
    @Autowired
    public WeeklyStatisticWriter() {
        this.globalCache = GlobalSingletonCache.getInstance();
    }
    @Override
    public void write(Chunk<? extends WeeklyVideoStatistic> chunk) throws Exception {
        for (WeeklyVideoStatistic stat : chunk) {

            globalCache.addWeeklyData(
                    VideoStatistic.builder()
                            .videoId(stat.getVideoId())
                            .weeklyViewCount(stat.getWeeklyViewCount())
                            .weeklyWatchedTime(stat.getWeeklyWatchedTime())
                            .build()
            ); // 데이터 전역 캐시에 추가
        }
    }

}
