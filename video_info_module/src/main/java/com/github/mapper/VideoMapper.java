package com.github.mapper;

import com.github.domain.VideoStatistic;
import com.github.dto.DailyBillDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

public interface VideoMapper {

    /**
     * email로 소유 videoId 리스트 반환
     * @param email
     * @return List<Integer> videoIdList
     */
    @Select("SELECT v.videoId " +
            "FROM Video v " +
            "WHERE v.email = #{email}")
    List<Integer> findVideoIdListByEmail(@Param("email") String email);


    /**
     * 일간 정산
     *
     * 1. email로 소유 video를 찾는다.
     * 2. 외래키 videoId를 통해 통계 테이블 join
     * @param email
     * @return List<DailyBillDto> videoId, 해당영상수익, 해당영상 광고수익의 리스트
     */
    @Select("SELECT v.videoId, vs.dailyVideoProfit, vs.dailyAdProfit, vs.createdAt " + // 공백 추가
            "FROM Video v " +
            "JOIN VideoStatistic vs " +
            "ON v.videoId = vs.videoId AND vs.createdAt = #{createdAt}" +
            "WHERE v.email = #{email}")
    @Results(value = {
            @Result(property = "videoId", column = "v.videoId"),
            @Result(property = "dailyVideoProfit", column = "vs.dailyVideoProfit"),
            @Result(property = "dailyAdProfit", column = "vs.dailyAdProfit"),
            @Result(property = "createdAt", column = "vs.createdAt")
    })
    List<DailyBillDto> findDailyProfitByEmail(@Param("email") String email, @Param("createdAt") LocalDate createdAt);

    /**
     * 기간별 정산
     *
     * @return List<VideoStatistic>
     */
    @Select("SELECT v.videoId, v.dailyVideoProfit, v.dailyAdProfit, v.createdAt " + // 공백 추가
            "FROM VideoStatistic v " +
            "WHERE v.videoId = #{videoId} " +
            "AND v.createdAt >= #{start} " +
            "AND v.createdAt <= #{end}")
    @Results(value = {
            @Result(property = "videoId", column = "v.videoId"),
            @Result(property = "dailyVideoProfit", column = "v.dailyVideoProfit"),
            @Result(property = "dailyAdProfit", column = "v.dailyAdProfit"),
            @Result(property = "createdAt", column = "v.createdAt")
    })
    List<DailyBillDto> findPeriodProfitByVideoId(
            @Param("videoId") Integer videoId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}
