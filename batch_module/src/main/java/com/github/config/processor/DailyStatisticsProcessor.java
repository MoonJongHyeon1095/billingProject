package com.github.config.processor;

import java.util.concurrent.ConcurrentHashMap;

import com.github.domain.VideoStatistic;
import org.springframework.batch.item.ItemProcessor;
import com.github.domain.WatchHistory;
import org.springframework.stereotype.Component;

@Component
public class DailyStatisticsProcessor implements ItemProcessor<WatchHistory, VideoStatistic> {
    // 동시성 문제를 방지하기 위해 ConcurrentHashMap 사용
    /**
     * final 키워드는 변수의 참조가 불변임을 의미합니다.
     * 즉, videoStatisticsCache 변수가 참조하는 ConcurrentHashMap 객체의 메모리 주소는 변경될 수 없습니다.
     * 하지만 ConcurrentHashMap 내부의 데이터는 변경될 수 있습니다.
     *
     * 메서드 호출시가 아니라 클래스 필드로 초기화:
     * final 키워드는 변수의 참조를 불변
     * 메서드 내에서 final 변수를 초기화하면, 메서드가 호출될 때마다 새로운 객체가 생성됩니다.
     * 이는 캐싱 객체가 호출될 때마다 새로 생성되는 문제를 발생
     * 캐싱 효과 저하: 메서드 호출 시마다 새로운 객체가 생성된다면, 캐싱 효과X
     * 따라서 videoStatisticsCache는 클래스 필드로 선언하고, 한 번만 초기화. 메서드 호출 시마다 새로운 객체가 생성되는 문제를 방지
     */
    private final ConcurrentHashMap<Integer, VideoStatistic> videoStatisticsCache = new ConcurrentHashMap<>();
    @Override
    public VideoStatistic process(WatchHistory item) throws Exception {
        final Integer videoId = item.getVideoId();
        // 캐시에서 VideoStatistic 가져오기
        videoStatisticsCache.compute(videoId, (key, videoStatistic) -> {
            if (videoStatistic == null) {
                // 캐시에 없으면 새로 생성
                return VideoStatistic.builder()
                        .videoId(videoId)
                        .dailyWatchedTime(Long.valueOf(item.getPlayedTime()))
                        .dailyViewCount(1)
                        .dailyAdViewCount(item.getAdViewCount())
                        .build();
            } else {
                // 캐시에 있으면 누적
                videoStatistic.setDailyWatchedTime(videoStatistic.getDailyWatchedTime() + item.getPlayedTime());
                videoStatistic.setDailyViewCount(videoStatistic.getDailyViewCount() + 1);
                videoStatistic.setDailyAdViewCount(videoStatistic.getDailyAdViewCount() + item.getAdViewCount());
                return videoStatistic;
            }
        });

        return videoStatisticsCache.get(videoId);
    }

    public void clearCache() {
        videoStatisticsCache.clear(); // 캐시 비우기
    }
}

