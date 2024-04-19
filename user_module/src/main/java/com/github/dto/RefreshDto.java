package com.github.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class RefreshDto {

    private String refreshToken;

    public RefreshDto(final String refreshToken) {
        this.refreshToken = refreshToken;
    }
}