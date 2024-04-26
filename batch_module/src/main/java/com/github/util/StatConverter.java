package com.github.util;

import com.github.domain.statistic.DailyVideoStatistic;
import com.github.domain.statistic.MonthlyVideoStatistic;
import com.github.domain.statistic.VideoStatistic;
import com.github.domain.statistic.WeeklyVideoStatistic;

public class StatConverter {

    // DailyVideoStatistic을 VideoStatistic으로 변환
    public static class DailyConverter {
        public static VideoStatistic convert(DailyVideoStatistic stat) {
            return VideoStatistic.builder()
                    .videoId(stat.getVideoId())
                    .dailyWatchedTime(stat.getDailyWatchedTime())
                    .dailyViewCount(stat.getDailyViewCount())
                    .build();
        }
    }

    // WeeklyVideoStatistic을 VideoStatistic으로 변환
    public static class WeeklyConverter {
        public static VideoStatistic convert(WeeklyVideoStatistic stat) {
            return VideoStatistic.builder()
                    .videoId(stat.getVideoId())
                    .weeklyWatchedTime(stat.getWeeklyWatchedTime())
                    .weeklyViewCount(stat.getWeeklyViewCount())
                    .build();
        }
    }

    // MonthlyVideoStatistic을 VideoStatistic으로 변환
    public static class MonthlyConverter {
        public static VideoStatistic convert(MonthlyVideoStatistic stat) {
            return VideoStatistic.builder()
                    .videoId(stat.getVideoId())
                    .monthlyWatchedTime(stat.getMonthlyWatchedTime())
                    .monthlyViewCount(stat.getMonthlyViewCount())
                    .build();
        }
    }
}
