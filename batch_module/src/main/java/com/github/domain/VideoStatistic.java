package com.github.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VideoStatistic {
    Integer videoStatisticId;
    Integer videoId;
    long dailyWatchedTime;
    long weeklyWatchedTime;
    long monthlyWatchedTime;
    long dailyViewCount;
    long weeklyViewCount;
    long monthlyViewCount;
}
