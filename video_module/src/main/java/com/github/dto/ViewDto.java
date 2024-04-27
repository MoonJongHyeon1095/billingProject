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
    private Optional<String> email = Optional.empty();

    public ViewDto(final int videoId, final String email) {
        this.videoId = videoId;
        this.email = Optional.ofNullable(email);
    }
}
