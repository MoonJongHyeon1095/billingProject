package com.github.service;

import com.github.controller.response.DailyTopResponse;
import com.github.dto.AccViewDto;
import com.github.dto.DailyViewTop5Dto;
import com.github.dto.DailyWatchedTimeTop5Dto;
import com.github.dto.PeriodViewDto;
import com.github.mapper.VideoMapper;
import com.github.mapper.VideoStatisticMapper;
import com.github.util.DateCalculator;
import com.github.util.GlobalSingletonCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatisticServiceTest {

    @Mock
    private VideoStatisticMapper videoStatisticMapper;
    @Mock
    private GlobalSingletonCache globalSingletonCache;

    @InjectMocks
    private StatisticService videoStatisticService;

    @BeforeEach
    void setUp() {
        // 예를 들어 필요에 따라 여기에서 기본 설정을 초기화할 수 있습니다.
    }

    @Test
    void findDailyTop5Test() {
        List<DailyViewTop5Dto> mockViewList = Arrays.asList(
                DailyViewTop5Dto.builder()
                        .videoId(1)
                        .dailyViewCount(100)
                        .title("testTitle")
                        .build()
        );

        List<DailyWatchedTimeTop5Dto> mockTimeList = Arrays.asList(
               DailyWatchedTimeTop5Dto.builder()
                       .videoId(1)
                       .dailyWatchedTime(1000)
                       .title("testTitle")
                       .build()
        );
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        when(videoStatisticMapper.findVideoOrderByViewCount(today)).thenReturn(mockViewList);
        when(videoStatisticMapper.findVideoOrderByDailyWatchedTime(today)).thenReturn(mockTimeList);

        DailyTopResponse result = videoStatisticService.findDailyTop5();

        assertNotNull(result);
        assertEquals(1, result.getViewList().size());
        assertEquals(1, result.getTimeList().size());

        verify(videoStatisticMapper).findVideoOrderByViewCount(today);
        verify(videoStatisticMapper).findVideoOrderByDailyWatchedTime(today);
    }

    @Test
    void testFindAllPeriodViewCountByDates() {
        LocalDate start = LocalDate.of(2024, 4, 2);
        LocalDate end = LocalDate.of(2024, 4, 3);
        List<PeriodViewDto> expectedStatistics = new ArrayList<>();
        expectedStatistics.add(PeriodViewDto.builder().videoId(1).dailyViewCount(150).build());
        expectedStatistics.add(PeriodViewDto.builder().videoId(1).dailyViewCount(300).build());

        /**
         * when (설정 단계)
         * 목적: 테스트 대상 코드가 실행될 때 필요한 조건을 모의(Mocking)하고, 특정 메서드 호출에 대해 예상되는 결과를 설정합니다.
         * 역할: 이 단계에서는 Mockito의 when() 메서드를 사용하여, 특정 메서드가 호출될 때 반환해야 할 결과를 정의합니다.
         * 이는 테스트를 위해 실제 데이터베이스나 외부 시스템에 의존하지 않고도, 메서드 호출의 결과를 시뮬레이션할 수 있게 합니다.
         */
        when(videoStatisticMapper.findAllPeriodViewCountByVideoId(start, end)).thenReturn(expectedStatistics);

        /**
         * Act (실행 단계)
         * 목적: 실제 로직이나 메서드를 실행합니다. 이 단계는 실제 코드가 어떻게 수행되는지를 테스트하며, 이를 위해 필요한 모든 호출을 포함합니다.
         * 역할: Act 단계에서는 설정된 모의 객체를 사용하여 서비스나 컨트롤러 등의 메서드를 호출하고, 결과를 받습니다. 이는 테스트를 위한 주된 실행 부분입니다.
         */
        List<PeriodViewDto> actualStatistics = videoStatisticService.findAllPeriodViewCount(start, end);

        /**
         * Assert (검증 단계)
         * 목적: 실행 단계에서 얻은 결과가 예상과 일치하는지 검증합니다. 이 단계는 테스트의 성공 또는 실패를 결정합니다.
         * 역할: JUnit의 assertEquals() 같은 검증 함수를 사용하여, 예상 결과와 실제 결과를 비교합니다. 모든 예상 조건이 만족됐는지 확인합니다.
         */
        assertEquals(expectedStatistics, actualStatistics);
        //메서드 호출 여부 검증
        verify(videoStatisticMapper).findAllPeriodViewCountByVideoId(start, end);
    }

    @Test
    void testGetWeeklyTop5() {
        LocalDate[] week = DateCalculator.getMonSun();
        List<PeriodViewDto> expectedStatistics = Arrays.asList(
               PeriodViewDto.builder().videoId(1).dailyViewCount(100).build(),
                PeriodViewDto.builder().videoId(2).dailyViewCount(200).build()
        );
        // ConcurrentHashMap의 모의 인스턴스 생성
        ConcurrentHashMap<Integer, Integer> mockCache = new ConcurrentHashMap<>();

        when(videoStatisticMapper.findAllPeriodViewCountByVideoId(week[0], week[1])).thenReturn(expectedStatistics);
        when(globalSingletonCache.getCacheData()).thenReturn(mockCache);

        videoStatisticService.getWeeklyTop5();

        verify(videoStatisticMapper).findAllPeriodViewCountByVideoId(week[0], week[1]);
        expectedStatistics.forEach(statistic ->
                verify(globalSingletonCache).addDailyData(statistic)
        );
        verify(globalSingletonCache).getCacheData();
        verify(globalSingletonCache).clearCache();
    }

    @Test
    void testGetMonthlyTop5() {
        LocalDate[] month = DateCalculator.getFirstAndLastDayOfMonth();
        List<PeriodViewDto> expectedStatistics = Arrays.asList(
                PeriodViewDto.builder().videoId(1).dailyViewCount(100).build(),
                PeriodViewDto.builder().videoId(2).dailyViewCount(200).build()
        );
        // ConcurrentHashMap의 모의 인스턴스 생성
        ConcurrentHashMap<Integer, Integer> mockCache = new ConcurrentHashMap<>();
        when(videoStatisticMapper.findAllPeriodViewCountByVideoId(month[0], month[1])).thenReturn(expectedStatistics);
        when(globalSingletonCache.getCacheData()).thenReturn(mockCache);
        videoStatisticService.getMonthlyTop5();

        verify(videoStatisticMapper).findAllPeriodViewCountByVideoId(month[0], month[1]);
        expectedStatistics.forEach(statistic ->
                verify(globalSingletonCache).addDailyData(statistic)
        );
        verify(globalSingletonCache).getCacheData();
        verify(globalSingletonCache).clearCache();
    }

    @Test
    void testGetTop5FromHash() {
        ConcurrentHashMap<Integer, Integer> testData = new ConcurrentHashMap<>();
        testData.put(1, 500);
        testData.put(2, 1500);
        testData.put(3, 300);
        testData.put(4, 800);
        testData.put(5, 1200);
        testData.put(6, 400);
        List<AccViewDto> result = videoStatisticService.getTop5FromHash(testData);

        assertEquals(5, result.size()); // 결과는 5개여야 합니다.
        assertTrue(result.stream().anyMatch(item -> item.getVideoId() == 2)); // videoId 2가 포함되어야 합니다.
        assertTrue(result.stream().anyMatch(item -> item.getVideoId() == 5)); // videoId 5가 포함되어야 합니다.
        assertTrue(result.stream().anyMatch(item -> item.getVideoId() == 4)); // videoId 4가 포함되어야 합니다.
        assertTrue(result.stream().anyMatch(item -> item.getVideoId() == 1)); // videoId 1이 포함되어야 합니다.
        assertTrue(result.stream().anyMatch(item -> item.getVideoId() == 6)); // videoId 6이 포함되어야 합니다.

        // 조회수가 높은 순으로 정렬되었는지 검증합니다.
        assertEquals(2, result.getFirst().getVideoId());
    }


}
