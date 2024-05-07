package com.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@Getter
@Builder
@JsonDeserialize(builder = PeriodViewTop5Dto.PeriodViewTop5DtoBuilder.class)
public class PeriodViewTop5Dto {
    @JsonProperty("videoId")
    private Integer videoId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("totalViewCount")
    private Integer totalViewCount;


}
