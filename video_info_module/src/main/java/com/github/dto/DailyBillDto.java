package com.github.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DailyBillDto {
    private Integer videoId;
    private Integer dailyVideoProfit;
    private Integer dailyAdProfit;
    private LocalDate createdAt;
}
