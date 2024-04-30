package com.github.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshResponse {
    private String accessToken;
    private String refreshToken;

    public static RefreshResponse from(final String accessToken, final String refreshToken){
        return new RefreshResponse(accessToken, refreshToken);
    }
}
