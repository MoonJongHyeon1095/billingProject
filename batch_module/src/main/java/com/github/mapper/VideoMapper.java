package com.github.mapper;

import com.github.domain.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface VideoMapper {
    // Find one video by videoId
    Optional<Video> findOneVideoById(Integer videoId);

    Optional<Integer> getTotalViewById(@Param("videoId") Integer videoId);


}
