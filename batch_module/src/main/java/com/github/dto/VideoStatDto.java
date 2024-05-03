package com.github.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class VideoStatDto {
    private Integer videoId;
    private Integer dailyViewCount;
    private Integer dailyWatchedTime;
    private Integer dailyAdViewCount;
    private LocalDate createdAt;
}
