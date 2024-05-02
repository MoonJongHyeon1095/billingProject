package com.github.mapper;

import com.github.domain.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.util.Optional;

@Mapper
public interface VideoMapper {
    @Select("SELECT * FROM Video WHERE videoId = #{videoId}")
    Optional<Video> findOneVideoById(@Param("videoId") Integer videoId);

    @Update(
            "UPDATE Video " +
                    "SET totalViewCount = #{video.totalViewCount} " +
                    "WHERE videoId = #{video.videoId}"
    )
    void updatedTotalViewCount(Video video);

    //비관적 락 //트랜잭션 레벨 기본 read committed
    @Select("SELECT totalViewCount FROM Video WHERE videoId = #{videoId} FOR UPDATE")
    Optional<Integer> findViewCountByVideoIdForUpdate(@Param("videoId") int videoId);

    @Update(
            "UPDATE Video " +
                    "SET totalViewCount = #{newViewCount} " +
                    "WHERE videoId = #{videoId}"
    )
    void updatedTotalViewCountPlusOne(@Param("newViewCount") int newViewCount, @Param("videoId") int videoId);

}
