package com.github.config.writer.statistic;

import com.github.domain.statistic.VideoStatistic;
import com.github.util.GlobalSingletonCache;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MonthlyStatisticWriter implements ItemWriter<VideoStatistic> {
    private final GlobalSingletonCache globalCache;

    // 생성자 주입
    @Autowired
    public MonthlyStatisticWriter() {
        this.globalCache = GlobalSingletonCache.getInstance();
    }
    @Override
    public void write(Chunk<? extends VideoStatistic> chunk) throws Exception {
        for (VideoStatistic stat : chunk) {

            globalCache.addMonthlyData(
                    VideoStatistic.builder()
                            .videoId(stat.getVideoId())
                            .monthlyViewCount(stat.getMonthlyViewCount())
                            .monthlyWatchedTime(stat.getMonthlyWatchedTime())
                            .monthlyAdViewCount(stat.getMonthlyViewCount())
                            .build()
            ); // 데이터 전역 캐시에 추가
        }
    }
}
