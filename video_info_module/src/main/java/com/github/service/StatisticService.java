package com.github.service;

import com.github.controller.response.DailyTopResponse;
import com.github.dto.DailyViewTop5Dto;
import com.github.dto.DailyWatchedTimeTop5Dto;
import com.github.mapper.VideoStatisticMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@AllArgsConstructor
public class StatisticService {
    private final VideoStatisticMapper videoStatisticMapper;
    public DailyTopResponse findDailyTop5(){
        List<DailyViewTop5Dto> viewList = findVideoOrderByDailyViewCount();
        List<DailyWatchedTimeTop5Dto> timeList = findVideoOrderByDailyWatchedTime();
        return DailyTopResponse.from(viewList, timeList);
    }

    private List<DailyViewTop5Dto> findVideoOrderByDailyViewCount(){
        //LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul");
        String today = "2024-04-05";

        return videoStatisticMapper.findVideoOrderByViewCount(LocalDate.parse(today));
    }

    private List<DailyWatchedTimeTop5Dto> findVideoOrderByDailyWatchedTime(){
        String today = "2024-04-05";

        return videoStatisticMapper.findVideoOrderByDailyWatchedTime(LocalDate.parse(today));
    }
}
