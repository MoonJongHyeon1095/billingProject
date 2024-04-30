package com.github.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@Builder
public class VideoStatistic {
    private Integer videoStatisticId;
    private Integer videoId;

    //재생시간 및 조회수 통계 배치작업
    private Long dailyWatchedTime; //분기에 따라 null이 되면, 직렬화 할 수 없다는 에러가 뜬다.
    private Integer dailyViewCount;
    private Integer dailyAdViewCount;
    //정산 배치작업
    private Integer dailyVideoProfit;
    private Integer dailyAdProfit;
    //private double zScore;
    private LocalDate createdAt;

}
