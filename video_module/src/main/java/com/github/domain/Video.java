package com.github.domain;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
@Getter
@Builder
public class Video {
    private Integer videoId;
    private Integer userId;
    private String title;
    private String desc;
    private String path;
    private Integer duration;
    private Integer viewCount;
    private Integer totalAdview;
    private Timestamp createdAt;
}
