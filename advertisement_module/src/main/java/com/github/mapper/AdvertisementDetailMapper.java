package com.github.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AdvertisementDetailMapper {
    @Select("SELECT COUNT(*) AS videoCount FROM AdvertisementDetail WHERE videoId = #{videoId}")
    Integer countByVideoId(@Param("videoId") Integer videoId);

    @Select(
            "SELECT adPriority FROM AdvertisementDetail " +
                    "WHERE videoId = #{videoId} " +
                    "ORDER BY adPriority ASC")
    List<Integer> findAllByVideoId(@Param("videoId") Integer videoId);

    @Update(
            "UPDATE AdvertisementDetail " +
                    "SET viewCount = viewCount + #{plusViewCount} " +
                    "WHERE videoId = #{videoId} AND adPriority = #{adPriority}")
    Integer updateViewCountByPriority(@Param("adPriority") Integer adPriority,
                                   @Param("videoId") Integer videoId,
                                   @Param("plusViewCount") Integer plusViewCount);
}
