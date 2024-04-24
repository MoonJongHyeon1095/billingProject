package com.github.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Statistic {
    private int videoId;
//    private long dailyWatchedTime; // 해당 비디오의 일간 총 시청 시간
//    private long weeklyWatchedTime; // 해당 비디오의 주간 총 시청 시간
//    private long monthlyWatchedTime;
    private long totalWatchTime;
    private long totalViewCount;
}

