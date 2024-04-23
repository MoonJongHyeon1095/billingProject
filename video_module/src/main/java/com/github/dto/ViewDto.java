package com.github.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@NoArgsConstructor
public class ViewDto {
    private int videoId;
    @Setter
    //로그인 한 경우만 존재
    private Optional<Integer> userId = Optional.empty();

    public ViewDto(final int videoId, final Integer userId) {
        this.videoId = videoId;
        this.userId = Optional.ofNullable(userId);
    }
}
