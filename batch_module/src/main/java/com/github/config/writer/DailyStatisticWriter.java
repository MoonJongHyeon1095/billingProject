package com.github.config.writer;

import com.github.domain.VideoStatistic;
import com.github.domain.WatchHistory;
import com.github.util.GlobalSingletonCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;


/**
 * Java에서 현재로서는 가상 스레드에 대한 이름 설정을 지원하지 않으며,
 * 가상 스레드의 API는 주로 매우 큰 수의 동시 작업을 경량화된 방식으로 처리하는 데 초점을 맞추고 있습니다.
 * 따라서, 작업을 추적하고 관리하는 데 있어서 로깅과 작업 관리 전략을 잘 설계하는 것이 중요합니다.
 */
@Slf4j
@Component
public class DailyStatisticWriter implements ItemWriter<WatchHistory> {
    private final GlobalSingletonCache globalCache;
    private static final ConcurrentHashMap<String, Integer> threadCountMap = new ConcurrentHashMap<>();
    // 생성자 주입
    @Autowired
    public DailyStatisticWriter() {
        this.globalCache = GlobalSingletonCache.getInstance();
    }


    @Override
    public void write(Chunk<? extends WatchHistory> chunk) throws Exception {
        String threadName = Thread.currentThread().getName();
        threadCountMap.compute(threadName, (key, value) -> (value == null) ? 1 : value + 1);
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

    public static void logThreadCounts() {
        threadCountMap.forEach((key, value) -> log.info("threadName: {} , executedTimes: {} times", key, value));
    }

    public static void clearThreadCounts() {
        threadCountMap.clear();  // ConcurrentHashMap의 모든 키와 값을 삭제
        log.info("All thread counts have been cleared.");
    }

}