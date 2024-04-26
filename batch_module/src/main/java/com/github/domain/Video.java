package com.github.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Video {
    private Integer videoId;
    private Integer userId;
    private String title;
    private String desc;
    private Integer duration;
    private Integer totalViewCount;
    private LocalDateTime createdAt;
}
