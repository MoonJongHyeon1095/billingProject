package com.github.domain.statistic;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MonthlyVideoStatistic {
    private Integer videoId;
    private long monthlyWatchedTime;
    private Integer monthlyViewCount;
}
