package com.github.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class VideoStatistic  {
    private Integer videoStatisticId;
    private Integer videoId;
    private long dailyWatchedTime; //분기에 따라 null이 되면, 직렬화 할 수 없다는 에러가 뜬다.
    private long weeklyWatchedTime;
    private long monthlyWatchedTime;
    private long dailyViewCount;
    private long weeklyViewCount;
    private long monthlyViewCount;
}
