package com.github.mapper;

import com.github.domain.statistic.VideoStatistic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface VideoStatisticMapper {
    void updateDailyStatistic(final VideoStatistic videoStatistic);
    void insertDailyStatistic(final VideoStatistic videoStatistic);

    void updateDailyBill(@Param("videoId")final int videoId, @Param("dailyBill")final int dailyBill);

    // videoId로 VideoStatistic 존재 여부 확인
    boolean existsVideoStatisticByVideoId(@Param("videoId") final int videoId);

    Optional<VideoStatistic> findOneByVideoId(@Param("videoId") final int videoId);
}
