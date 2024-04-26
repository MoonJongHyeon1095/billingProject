package com.github.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Advertisement {
    private Integer advertisementId;
    private Integer adPriority;
}
