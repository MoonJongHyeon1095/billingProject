package com.github.mapper;

import com.github.domain.Video;

import java.util.List;
import java.util.Optional;

public interface VideoMapper {
    // Find one video by videoId
    Optional<Video> findOneVideoById(Integer videoId);

    // Find all videos by userId
    List<Video> findAllVideoByUserId(Integer userId);

    // Find all videos by title
    List<Video> findAllVideoByTitle(String title);

    void updatedTotalViewCount(Video video);
}
