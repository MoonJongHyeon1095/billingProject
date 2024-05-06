package com.github.util;

import com.github.dto.PeriodViewDto;
import com.github.mapper.VideoStatisticMapper;
import com.github.service.StatisticService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class GlobalSingletonCacheTest {

    @BeforeEach
    void setUp() {
        // 예를 들어 필요에 따라 여기에서 기본 설정을 초기화할 수 있습니다.
    }

    @Test
    public void testSingletonInstance() {
        // 싱글톤 인스턴스 테스트
        GlobalSingletonCache instance1 = GlobalSingletonCache.getInstance();
        GlobalSingletonCache instance2 = GlobalSingletonCache.getInstance();

        assertNotNull(instance1);
        assertSame(instance1, instance2);
    }

    @Test
    public void testAddDailyData() {
        // 데이터 추가 테스트
        GlobalSingletonCache cache = GlobalSingletonCache.getInstance();
        cache.clearCache(); // 캐시 초기화

        PeriodViewDto data1 = PeriodViewDto.builder().videoId(1).dailyViewCount(100).build();
        PeriodViewDto data2 = PeriodViewDto.builder().videoId(1).dailyViewCount(200).build();

        cache.addDailyData(data1);
        cache.addDailyData(data2);

        // 비디오 ID 1에 대한 조회 수는 300이어야 함
        assertEquals(300, cache.getCacheData().get(1).intValue());
    }

    @Test
    public void testCacheSize() {
        // 캐시 사이즈 테스트
        GlobalSingletonCache cache = GlobalSingletonCache.getInstance();
        cache.clearCache(); // 캐시 초기화

        PeriodViewDto data1 = PeriodViewDto.builder().videoId(1).dailyViewCount(100).build();
        PeriodViewDto data2 = PeriodViewDto.builder().videoId(1).dailyViewCount(200).build();

        cache.addDailyData(data1);
        cache.addDailyData(data2);

        // 캐시에는 1 개의 엔트리가 있어야 함
        assertEquals(1, cache.getCacheSize());
    }
}
