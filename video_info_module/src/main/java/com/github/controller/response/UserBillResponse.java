package com.github.controller.response;

import com.github.domain.VideoStatistic;
import com.github.dto.DailyBillDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserBillResponse {
    private final List<DailyBillDto> videoStatisticList;
    public static UserBillResponse from(final List<DailyBillDto> videoStat) {
        return UserBillResponse.builder()
                .videoStatisticList(videoStat)
                .build();
    }

}
