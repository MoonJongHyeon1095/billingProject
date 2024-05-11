package com.github.util;

import com.github.domain.VideoStatistic;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class GlobalSingletonCache {
    private static volatile GlobalSingletonCache globalSingletonCache;
    private ConcurrentHashMap<Integer, VideoStatistic> cacheData;


    private GlobalSingletonCache() {
        cacheData = new ConcurrentHashMap<Integer, VideoStatistic>();
    }

    /**
     * 더블-체크드 로킹 (Double-Checked Locking):
     * 싱글톤 패턴에서 일반적으로 사용되는 기법
     * 인스턴스를 두 번 확인하여 인스턴스 생성 시 오버헤드를 최소화
     * 첫 번째 확인에서는 동기화 없이 인스턴스가 존재하는지 확인, 두 번째 확인에서는 동기화된 블록 내에서 인스턴스가 존재하는지 확인. 인스턴스가 없을 때만 동기화 블록을 실행
     *
     * JVM과 컴파일러는 최적화를 위해 명령어 재정렬을 수행할 수 있다. 이는 성능을 향상시키지만, 멀티스레드 환경에서 예기치 않은 결과를 초래.
     * 예를 들어, 아래와 같은 순서로 인스턴스가 생성된다면...
     * 1. 메모리 할당
     * 2. 변수에 할당
     * 3. 생성자 실행
     * 이러한 재정렬이 발생하면, 다른 스레드가 첫 번째 체크에서 instance가 null이 아니라고 확인할 수 있지만, 실제로 생성자가 아직 실행되지 않은 상태일 수 있다.
     * 이는 부분적으로 생성된 인스턴스를 다른 스레드가 읽을 수 있음을 의미하며, 심각한 동시성 문제를 유발.
     *
     * volatile은 JVM과 컴파일러에게 특정 변수의 재정렬을 방지, 모든 스레드가 해당 변수의 최신 값을 보도록 지시.
     * 더블 체크드 로킹에서 volatile을 사용하면 인스턴스가 올바르게 생성, 모든 스레드가 완전히 초기화된 인스턴스를 보장.
     *
     * volatile 없이 더블 체크드 로킹을 구현하는 것은 잠재적인 위험을 안고 있다.  동시성을 고려하는 경우 volatile 키워드를 사용하여 더블 체크드 로킹을 구현하는 것이 안전.
     */
    public static GlobalSingletonCache getInstance() {
        if (globalSingletonCache == null) { // 첫 번째 체크
            synchronized (GlobalSingletonCache.class) {
                if (globalSingletonCache == null) { // 두 번째 체크
                    globalSingletonCache = new GlobalSingletonCache();
                }
            }
        }
        return globalSingletonCache;
    }

    // VideoStatistic 객체를 추가하는 메소드
    public void addDailyData(VideoStatistic data) {
        int videoId = data.getVideoId();
        if (cacheData.containsKey(videoId)) { // 존재 여부를 확인
            VideoStatistic existingData = cacheData.get(videoId);
            // 필요한 경우 통계 업데이트
            existingData.setDailyWatchedTime(existingData.getDailyWatchedTime() + data.getDailyWatchedTime());
            existingData.setDailyViewCount(existingData.getDailyViewCount() + data.getDailyViewCount());
            existingData.setDailyAdViewCount(existingData.getDailyAdViewCount() + data.getDailyAdViewCount());
        } else {
            cacheData.put(videoId, data);
        }
    }

    // 전체 캐시 데이터를 반환하는 메소드
    public List<VideoStatistic> getCacheData() {
        return new ArrayList<>(cacheData.values()); // 모든 캐시 데이터를 List로 반환
    }

    public void clearCache() {
        cacheData.clear();
    }

    public int getCacheSize() {
        return cacheData.size();
    }
}
