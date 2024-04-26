package com.github.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdvertisementDetail {
    private Integer adDetailId;
    private Integer adPriority;
    private Integer videoId;
    private Integer viewCount;
}
