package com.github.domain.statistic;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DailyVideoStatistic {
    private Integer videoId;
    private long dailyWatchedTime; //분기에 따라 null이 되면, 직렬화 할 수 없다는 에러가 뜬다.
    private Integer dailyViewCount;
}
