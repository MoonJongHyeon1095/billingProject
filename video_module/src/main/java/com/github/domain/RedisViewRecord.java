package com.github.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RedisViewRecord {
    String videoId;
    Integer viewCount;
    Integer deltaView;
}
