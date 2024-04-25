package com.github.config.processor;

import com.github.domain.VideoStatistic;
import com.github.domain.WatchHistory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WeeklyStatisticsProcessor implements ItemProcessor<WatchHistory, VideoStatistic> {
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
    final private ConcurrentHashMap<Integer, VideoStatistic> videoStatisticsCache = new ConcurrentHashMap<>();
    @Override
    public VideoStatistic process(final WatchHistory item) throws Exception {
        final Integer videoId = item.getVideoId();

        // 캐시에서 VideoStatistic 가져오기
        videoStatisticsCache.compute(videoId, (key, videoStatistic) -> {
            if (videoStatistic == null) {
                // 캐시에 없으면 새로 생성
                return VideoStatistic.builder()
                        .videoId(videoId)
                        .weeklyWatchedTime(item.getPlayedTime())
                        .weeklyViewCount(1)
                        .build();
            } else {
                // 캐시에 있으면 누적
                videoStatistic.setWeeklyWatchedTime(videoStatistic.getWeeklyWatchedTime() + item.getPlayedTime());
                videoStatistic.setWeeklyViewCount(videoStatistic.getWeeklyViewCount() + 1);
                return videoStatistic;
            }
        });

        return videoStatisticsCache.get(videoId);
    }

    public void clearCache() {
        videoStatisticsCache.clear(); // 캐시 비우기
    }
}
