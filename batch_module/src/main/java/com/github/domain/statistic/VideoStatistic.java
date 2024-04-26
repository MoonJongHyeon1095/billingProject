package com.github.domain.statistic;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class VideoStatistic  {
    private Integer videoStatisticId;
    private Integer videoId;
    //재생시간 및 조회수 통계 배치작업
    private Long dailyWatchedTime; //분기에 따라 null이 되면, 직렬화 할 수 없다는 에러가 뜬다.
    private Long weeklyWatchedTime;
    private Long monthlyWatchedTime;

    private Integer dailyViewCount;
    private Integer weeklyViewCount;
    private Integer monthlyViewCount;

    private Integer dailyAdViewCount;
    private Integer weeklyAdViewCount;
    private Integer monthlyAdViewCount;

    //정산 배치작업
    private Integer dailyBill;
    private Integer weeklyBill;
    private Integer monthlyBill;

}
