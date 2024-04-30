package com.github.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeeklyBillDto {
    private Integer videoId;
    private Integer weeklyVideoProfit;
    private Integer weeklyAdProfit;
}
