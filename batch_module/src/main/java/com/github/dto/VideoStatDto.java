package com.github.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class VideoStatDto {
    private Integer videoId;
    private Integer dailyViewCount;
    private Long dailyWatchedTime;
    private Integer dailyAdViewCount;
    private LocalDate createdAt;
}
