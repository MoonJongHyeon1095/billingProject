package com.github.mapper;

import com.github.domain.VideoStatistic;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StatisticMapper {
    void upsertDailyStatistic(VideoStatistic videoStatistic);
    void upsertWeeklyStatistic(VideoStatistic videoStatistic);
    void upsertMonthlyStatistic(VideoStatistic videoStatistic);
}
