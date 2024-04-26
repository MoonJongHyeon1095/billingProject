package com.github.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RedisViewRecord {
    String key; //"video:" + videoId
    Integer viewCount;
    Integer increment;
}
