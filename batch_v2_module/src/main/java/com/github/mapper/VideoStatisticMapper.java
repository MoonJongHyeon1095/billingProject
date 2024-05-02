package com.github.mapper;

import com.github.domain.VideoStatistic;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.Optional;

@Mapper
public interface VideoStatisticMapper {
    @Update(
            "UPDATE VideoStatistic " +
                    "SET dailyWatchedTime = #{dailyWatchedTime}, " +
                    "dailyViewCount = #{dailyViewCount}, " +
                    "dailyAdViewCount = #{dailyAdViewCount} " +
                    "WHERE createdAt = #{createdAt} AND videoId = #{videoId}")
    void updateDailyStatistic(final VideoStatistic videoStatistic);

    @Insert(
            "INSERT INTO VideoStatistic " +
                    "(videoId, dailyWatchedTime, dailyViewCount, dailyAdViewCount, createdAt) " +
                    "VALUES (#{videoId}, #{dailyWatchedTime}, #{dailyViewCount}, #{dailyAdViewCount}, #{createdAt})"
    )
    void insertDailyStatistic(final VideoStatistic videoStatistic);

    @Update(
            "UPDATE VideoStatistic " +
                    "SET dailyVideoProfit = #{dailyVideoProfit}, " +
                    "dailyAdProfit = #{dailyAdProfit} " +
                    "WHERE createdAt = #{createdAt} AND videoId = #{videoId}"
    )
    void updateDailyBill(@Param("videoId")final Integer videoId,
                         @Param("dailyVideoProfit")final Integer dailyVideoProfit,
                         @Param("dailyAdProfit")final Integer dailyAdProfit,
                         @Param("createdAt")final LocalDate createdAt);

    // videoId로 VideoStatistic 존재 여부 확인
    @Select(
            "SELECT EXISTS " +
                    "(SELECT 1 FROM VideoStatistic " +
                    "WHERE videoId = #{videoId})"
    )
    boolean existsVideoStatisticByVideoId(@Param("videoId") final int videoId);

    @Select(
            "SELECT * FROM VideoStatistic " +
                    "WHERE videoId = #{videoId}"
    )
    Optional<VideoStatistic> findOneByVideoId(@Param("videoId") final int videoId);
}
