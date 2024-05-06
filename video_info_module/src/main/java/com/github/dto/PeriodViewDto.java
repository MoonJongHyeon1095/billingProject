package com.github.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PeriodViewDto {
    private Integer videoId;
    private Integer dailyViewCount;
}
