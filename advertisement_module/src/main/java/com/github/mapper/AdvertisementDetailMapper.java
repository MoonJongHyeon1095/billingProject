package com.github.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdvertisementDetailMapper {

    Integer countByVideoId(@Param("videoId") Integer videoId);

    List<Integer> findAllByVideoId(@Param("videoId") Integer videoId);

    Integer updateViewCountByPriority(@Param("adPriority") Integer adPriority,
                                   @Param("videoId") Integer videoId,
                                   @Param("plusViewCount") Integer plusViewCount);
}
