package com.github.controller.response;

import com.github.domain.VideoStatistic;
import com.github.dto.DailyViewTop5Dto;
import com.github.dto.DailyWatchedTimeTop5Dto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DailyTopResponse {
    private final List<DailyWatchedTimeTop5Dto> timeList;
    private final List<DailyViewTop5Dto> viewList;
    public static DailyTopResponse from(final List<DailyViewTop5Dto> viewList, List<DailyWatchedTimeTop5Dto> timeList) {
        return DailyTopResponse.builder()
                .viewList(viewList)
                .timeList(timeList)
                .build();
    }
}
