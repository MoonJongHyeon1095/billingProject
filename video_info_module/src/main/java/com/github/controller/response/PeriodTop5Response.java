package com.github.controller.response;

import com.github.dto.DailyViewTop5Dto;
import com.github.dto.DailyWatchedTimeTop5Dto;
import com.github.dto.PeriodViewTop5Dto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PeriodTop5Response {
    private final List<PeriodViewTop5Dto> viewList;
    public static PeriodTop5Response from(final List<PeriodViewTop5Dto> viewList) {
        return PeriodTop5Response.builder()
                .viewList(viewList)
                .build();
    }
}
