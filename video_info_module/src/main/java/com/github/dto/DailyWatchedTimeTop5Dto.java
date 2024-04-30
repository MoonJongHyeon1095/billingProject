package com.github.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DailyWatchedTimeTop5Dto {
    private Integer videoId;
    private String title;
    private Integer dailyWatchedTime;
}
