package com.github.mapper;

import com.github.domain.VideoStatistic;
import com.github.dto.DailyBillDto;
import com.github.dto.DailyViewTop5Dto;
import com.github.dto.DailyWatchedTimeTop5Dto;
import com.github.dto.PeriodViewDto;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Mapper
public interface VideoStatisticMapper {



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


    /**
     * 일간 조회수 탑5
     */
    @Select(
            "SELECT v.videoId, v.title, vs.dailyViewCount FROM VideoStatistic vs " +
                    "JOIN Video v ON vs.videoId = v.videoId " +
                    "WHERE vs.createdAt = #{createdAt} " +
                    "ORDER BY vs.dailyViewCount DESC " + // 조회수가 많은 순으로 정렬
                    "LIMIT 5"
    )
    @Results(value = {
            @Result(property = "videoId", column = "videoId"),
            @Result(property = "title", column = "title"),
            @Result(property = "dailyViewCount", column = "dailyViewCount"),
    })
    List<DailyViewTop5Dto> findVideoOrderByViewCount(@Param("createdAt") final LocalDate createdAt);


    /**
     * 일간 재생시간 탑5
     */
    @Select(
            "SELECT v.videoId, v.title, vs.dailyWatchedTime FROM VideoStatistic vs " +
                    "JOIN Video v ON vs.videoId = v.videoId " +
                    "WHERE vs.createdAt = #{createdAt} " +
                    "ORDER BY vs.dailyWatchedTime DESC " + // 조회수가 많은 순으로 정렬
                    "LIMIT 5"
    )
    @Results(value = {
            @Result(property = "videoId", column = "videoId"),
            @Result(property = "title", column = "title"),
            @Result(property = "dailyWatchedTime", column = "dailyWatchedTime"),
    })
    List<DailyWatchedTimeTop5Dto> findVideoOrderByDailyWatchedTime(@Param("createdAt") final LocalDate createdAt);

    /**
     * 기간별 조회수
     *
     * @return List<PeriodViewDto>
     */
    @Select("SELECT v.videoId, v.dailyViewCount " +
            "FROM VideoStatistic v " +
            "WHERE v.createdAt >= #{start} " +
            "AND v.createdAt <= #{end}")
    @Results(value = {
            @Result(property = "dailyViewCount", column = "v.dailyViewCount")
    })
    List<PeriodViewDto> findAllPeriodViewCountByVideoId(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}
