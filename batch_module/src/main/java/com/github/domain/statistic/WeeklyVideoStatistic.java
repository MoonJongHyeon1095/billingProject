package com.github.domain.statistic;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class WeeklyVideoStatistic {
    private Integer videoId;
    private long weeklyWatchedTime;
    private Integer weeklyViewCount;
}
