package com.github.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.controller.response.DailyTopResponse;
import com.github.controller.response.PeriodTop5Response;
import com.github.dto.*;
import com.github.exception.VideoErrorCode;
import com.github.exception.VideoException;
import com.github.mapper.VideoMapper;
import com.github.mapper.VideoStatisticMapper;
import com.github.repository.RedisInfoRepository;
import com.github.util.DateCalculator;
import com.github.util.GlobalSingletonCache;
import com.github.util.Serializer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class StatisticService {
    private final VideoMapper videoMapper;
    private final VideoStatisticMapper videoStatisticMapper;
    private final GlobalSingletonCache globalCache;
    private final RedisInfoRepository redisInfoRepository;

    public StatisticService(VideoMapper videoMapper, VideoStatisticMapper videoStatisticMapper, RedisInfoRepository redisInfoRepository) {
        this.videoMapper = videoMapper;
        this.videoStatisticMapper = videoStatisticMapper;
        this.redisInfoRepository = redisInfoRepository;
        this.globalCache = GlobalSingletonCache.getInstance();
    }

    public DailyTopResponse findDailyTop5(){
        List<DailyViewTop5Dto> viewList = findVideoOrderByDailyViewCount();
        List<DailyWatchedTimeTop5Dto> timeList = findVideoOrderByDailyWatchedTime();
        return DailyTopResponse.from(viewList, timeList);
    }

    public PeriodTop5Response findWeeklyTop5() {
        String key = "weeklyViewCount";
        try {
            Optional<String> result = redisInfoRepository.getByKey(key);
            if(result.isPresent()) {
                List<PeriodViewTop5Dto> dtoList = Serializer.deserializeJsonToList(result.get());
                return PeriodTop5Response.from(dtoList);
            } else {
                List<PeriodViewTop5Dto> weeklyTop5 = getWeeklyTop5WithTitle();
                redisInfoRepository.save(key, Serializer.serializeListToJson(weeklyTop5), 86400);
                return PeriodTop5Response.from(weeklyTop5);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error processing JSON data", e);
        }
    }

    public PeriodTop5Response findMonthlyTop5(){
        String key = "monthlyViewCount";
        try {
            Optional<String> result = redisInfoRepository.getByKey(key);
            if(result.isPresent()) {
                List<PeriodViewTop5Dto> dtoList = Serializer.deserializeJsonToList(result.get());
                return PeriodTop5Response.from(dtoList);
            } else {
                List<PeriodViewTop5Dto> monthlyTop5 = getMonthlyTop5WithTitle();
                redisInfoRepository.save(key, Serializer.serializeListToJson(monthlyTop5), 86400);
                return PeriodTop5Response.from(monthlyTop5);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error processing JSON data", e);
        }
    }

    protected List<PeriodViewTop5Dto> getWeeklyTop5WithTitle(){
        List<AccViewDto> top5 = getWeeklyTop5();
        List<PeriodViewTop5Dto> weeklyTop5 = new ArrayList<>();
        top5.forEach((dto)->{
            String title = findVideoTitleByVideoId(dto.getVideoId());
            weeklyTop5.add(PeriodViewTop5Dto.builder()
                    .videoId(dto.getVideoId())
                    .title(title)
                    .totalViewCount(dto.getTotalViewCount())
                    .build());
        });
        return weeklyTop5;
    }

    protected List<PeriodViewTop5Dto> getMonthlyTop5WithTitle(){
        List<AccViewDto> top5 = getMonthlyTop5();
        List<PeriodViewTop5Dto> monthlyTop5 = new ArrayList<>();
        top5.forEach((dto)->{
            String title = findVideoTitleByVideoId(dto.getVideoId());
            monthlyTop5.add(PeriodViewTop5Dto.builder()
                    .videoId(dto.getVideoId())
                    .title(title)
                    .totalViewCount(dto.getTotalViewCount())
                    .build());
        });
        return monthlyTop5;
    }

    protected List<AccViewDto> getWeeklyTop5(){
        LocalDate[] monDaySunDay = DateCalculator.getMonSun();
        List<PeriodViewDto> viewList = findAllPeriodViewCount(monDaySunDay[0], monDaySunDay[1]);

        for(PeriodViewDto data : viewList){
            globalCache.addDailyData(data);
        }
        //주의! 새로운 객체로 복사된게 아니다. 그냥 매모리 참조 가져온 것.
        ConcurrentHashMap<Integer, Integer> cache = globalCache.getCacheData();

        List<AccViewDto> result = getTop5FromHash(cache);
        globalCache.clearCache();
        return result;
    }

    protected List<AccViewDto> getMonthlyTop5(){
        LocalDate[] firstAndLastDayOfMonth = DateCalculator.getFirstAndLastDayOfMonth();
        List<PeriodViewDto> viewList = findAllPeriodViewCount(firstAndLastDayOfMonth[0], firstAndLastDayOfMonth[1]);

        for(PeriodViewDto data : viewList){
            globalCache.addDailyData(data);
        }

        ConcurrentHashMap<Integer, Integer> cache = globalCache.getCacheData();
        List<AccViewDto> result = getTop5FromHash(cache);
        globalCache.clearCache();

        return result;
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
        log.info("getFromHash: {}", viewList.get(0));
        return viewList.subList(0, Math.min(5, viewList.size()));
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

    @Transactional(readOnly = true)
    private String findVideoTitleByVideoId(final int videoId){
        return videoMapper.findVideoTitleByVideoId(videoId).orElseThrow(()->new VideoException(VideoErrorCode.VIDEO_NOT_FOUND));
    }
}
