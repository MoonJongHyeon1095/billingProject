package com.github.mapper;

import com.github.domain.VideoStatistic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface VideoStatisticMapper {
    void upsertDailyStatistic(final VideoStatistic videoStatistic);
    void upsertWeeklyStatistic(final VideoStatistic videoStatistic);
    void upsertMonthlyStatistic(final VideoStatistic videoStatistic);

    void updateDailyStatistic(final VideoStatistic videoStatistic);
    void insertDailyStatistic(final VideoStatistic videoStatistic);
    void updateWeeklyStatistic(final VideoStatistic videoStatistic);
    void insertWeeklyStatistic(final VideoStatistic videoStatistic);
    void updateMonthlyStatistic(final VideoStatistic videoStatistic);
    void insertMonthlyStatistic(final VideoStatistic videoStatistic);

    // videoId로 VideoStatistic 존재 여부 확인
    boolean existsVideoStatisticByVideoId(@Param("videoId") final int videoId);

    Optional<VideoStatistic> findOneByVideoId(@Param("videoId") final int videoId);
}
