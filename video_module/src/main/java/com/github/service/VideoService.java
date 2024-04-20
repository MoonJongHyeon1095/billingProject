package com.github.service;

import com.github.domain.Video;
import com.github.dto.StreamDto;
import com.github.exception.VideoErrorCode;
import com.github.exception.VideoException;
import com.github.mapper.VideoMapper;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class VideoService {
    private final VideoMapper videoMapper;

    @Transactional
    public void countView(Video video){
        Video.builder()
                .videoId(video.getVideoId())
                .viewCount(video.getViewCount()+1)
                .build();
        videoMapper.updateViewCount(video);
    }

    @Transactional
    protected Video findVideoById(final int videoId){
        return videoMapper.findOneVideoById(videoId).orElseThrow(()->new VideoException(VideoErrorCode.VIDEO_NOT_FOUND));
    }

}
