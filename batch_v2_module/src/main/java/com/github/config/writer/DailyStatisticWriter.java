package com.github.config.writer;

import com.github.domain.VideoStatistic;
import com.github.domain.WatchHistory;
import com.github.util.GlobalSingletonCache;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class DailyStatisticWriter implements ItemWriter<WatchHistory> {
    private final GlobalSingletonCache globalCache;

    // 생성자 주입
    @Autowired
    public DailyStatisticWriter() {
        this.globalCache = GlobalSingletonCache.getInstance();
    }
    @Override
    public void write(Chunk<? extends WatchHistory> chunk) throws Exception {
        for (WatchHistory stat : chunk) {
            globalCache.addDailyData(
                    //stat
                    VideoStatistic.builder()
                        .videoId(stat.getVideoId())
                        .dailyViewCount(1)
                        .dailyWatchedTime(Long.valueOf(stat.getPlayedTime()))
                        .dailyAdViewCount(stat.getAdViewCount())
                        .build()
            ); // 데이터 전역 캐시에 추가
        }
    }
}