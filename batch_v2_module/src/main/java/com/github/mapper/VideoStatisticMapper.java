package com.github.mapper;

import com.github.domain.VideoStatistic;
import com.github.dto.VideoStatDto;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

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
            "SELECT videoId, dailyViewCount, dailyWatchedTime, dailyAdViewCount, createdAt FROM VideoStatistic " +
                    "WHERE createdAt = #{createdAt} AND videoId = #{videoId}"
    )
    @Results(value = {
            @Result(property = "videoId", column = "videoId"),
            @Result(property = "dailyViewCount", column = "dailyViewCount"),
            @Result(property = "dailyWatchedTime", column = "dailyWatchedTime"),
            @Result(property = "dailyAdViewCount", column = "dailyAdViewCount"),
            @Result(property = "createdAt", column = "createdAt", javaType = LocalDate.class, jdbcType = JdbcType.DATE)
    })
    Optional<VideoStatDto> findOneByVideoIdAndCreatedAt(
            @Param("createdAt") final LocalDate createdAt,
            @Param("videoId") final int videoId
    );
}
