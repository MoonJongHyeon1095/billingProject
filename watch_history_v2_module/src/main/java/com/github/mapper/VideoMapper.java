package com.github.mapper;

import com.github.domain.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.Optional;

@Mapper
public interface VideoMapper {
    @Select("SELECT * FROM Video WHERE videoId = #{videoId}")
    Optional<Video> findOneVideoById(Integer videoId);

    @Update(
            "UPDATE Video " +
                    "SET totalViewCount = #{video.totalViewCount} " +
                    "WHERE videoId = #{video.videoId}"
    )
    void updatedTotalViewCount(Video video);

    @Select(
            "SELECT numericOrderKey FROM WatchHistory " +
                    "WHERE createdAt = #{createdAt} " +
                    "ORDER BY numericOrderKey DESC LIMIT 1")
    Integer findMaxNumericOrderByCreatedAt(LocalDate createdAt);
}
