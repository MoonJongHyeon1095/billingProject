package com.github.domain;

import lombok.Builder;

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
