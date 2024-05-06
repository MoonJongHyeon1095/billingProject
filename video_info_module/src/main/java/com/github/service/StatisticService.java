package com.github.service;

import com.github.controller.response.DailyTopResponse;
import com.github.dto.AccViewDto;
import com.github.dto.DailyViewTop5Dto;
import com.github.dto.DailyWatchedTimeTop5Dto;
import com.github.dto.PeriodViewDto;
import com.github.mapper.VideoStatisticMapper;
import com.github.util.DateCalculator;
import com.github.util.GlobalSingletonCache;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class StatisticService {
    private final VideoStatisticMapper videoStatisticMapper;
    private final GlobalSingletonCache globalCache;

    @Transactional(readOnly = true)
    public DailyTopResponse findDailyTop5(){
        List<DailyViewTop5Dto> viewList = findVideoOrderByDailyViewCount();
        List<DailyWatchedTimeTop5Dto> timeList = findVideoOrderByDailyWatchedTime();
        return DailyTopResponse.from(viewList, timeList);
    }

    protected void getWeeklyTop5(){
        LocalDate[] monDaySunDay = DateCalculator.getMonSun();
        List<PeriodViewDto> viewList = findAllPeriodViewCount(monDaySunDay[0], monDaySunDay[1]);

        for(PeriodViewDto data : viewList){
            globalCache.addDailyData(data);
        }

        ConcurrentHashMap<Integer, Integer> cache = globalCache.getCacheData();
        globalCache.clearCache();
        getTop5FromHash(cache);
    }

    protected void getMonthlyTop5(){
        LocalDate[] firstAndLastDayOfMonth = DateCalculator.getFirstAndLastDayOfMonth();
        List<PeriodViewDto> viewList = findAllPeriodViewCount(firstAndLastDayOfMonth[0], firstAndLastDayOfMonth[1]);

        for(PeriodViewDto data : viewList){
            globalCache.addDailyData(data);
        }

        ConcurrentHashMap<Integer, Integer> cache = globalCache.getCacheData();
        globalCache.clearCache();
        getTop5FromHash(cache);
    }


    @Transactional(readOnly = true)
    protected List<DailyViewTop5Dto> findVideoOrderByDailyViewCount(){
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
//        String today = "2024-04-05";

        return videoStatisticMapper.findVideoOrderByViewCount(today);
    }
    @Transactional(readOnly = true)
    private List<DailyWatchedTimeTop5Dto> findVideoOrderByDailyWatchedTime(){
//        String today = "2024-04-05";
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        return videoStatisticMapper.findVideoOrderByDailyWatchedTime(today);
    }

    @Transactional(readOnly = true)
    protected List<PeriodViewDto> findAllPeriodViewCount(LocalDate start, LocalDate end) {
        return videoStatisticMapper.findAllPeriodViewCountByVideoId(start, end);
    }

    protected List<AccViewDto> getTop5FromHash(ConcurrentHashMap<Integer, Integer> cache){
        List<AccViewDto> viewList = new ArrayList<>();

        cache.forEach((videoId, totalViewCount) -> {
            viewList.add(AccViewDto.builder()
                    .videoId(videoId)
                    .totalViewCount(totalViewCount)
                    .build());
        });
        viewList.sort(Comparator.comparingInt(AccViewDto::getTotalViewCount).reversed());
        return viewList.subList(0, Math.min(5, viewList.size()));
    }
}
