package com.github.mapper;

import com.github.domain.Video;
import com.github.dto.VideoRangeDto;
import com.github.dto.VideoViewsDto;
import org.apache.ibatis.annotations.*;

import java.util.Optional;

@Mapper
public interface VideoMapper {
    @Select(
            "SELECT totalViewCount, totalAdViewCount " +
                    //"SELECT * " +
                    "FROM Video " +
                    "WHERE videoId = #{videoId}")
    @Results({
            @Result(column = "totalViewCount", property = "totalViewCount"),
            @Result(column = "totalAdViewCount", property = "totalAdViewCount"),
    })
    Optional<VideoViewsDto> getTotalViewsAndTotalAdViewsById(@Param("videoId") Integer videoId);

    @Select(
            "SELECT MIN(videoId) AS firstVideoId, MAX(videoId) AS lastVideoId " +
                    "FROM Video")
    VideoRangeDto findFirstAndLastVideoId();

}
