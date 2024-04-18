package com.github.controller.response;

import lombok.*;

@Getter
@Builder
public class LoginResponse {

    private final String accessToken;
    private final String refreshToken;

//    public LoginResponse(final String accessToken, final String refreshToken) {
//        this.accessToken = accessToken;
//        this.refreshToken = refreshToken;
//    }

    public static LoginResponse from(final String accessToken, final String refreshToken) {
        return new LoginResponse(accessToken, refreshToken);
    }

    public static LoginResponse from(final String accessToken) {
        return LoginResponse.builder().accessToken(accessToken).build();
    }
}