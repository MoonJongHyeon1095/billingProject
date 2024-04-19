package com.github.controller.response;

import lombok.*;

@Getter
@Builder
public class LoginResponse {

    private final String accessToken;
    private final String refreshToken;
    private final String message;

//    public LoginResponse(final String accessToken, final String refreshToken) {
//        this.accessToken = accessToken;
//        this.refreshToken = refreshToken;
//    }

    public static LoginResponse from(final String accessToken, final String refreshToken, final String message) {
        return new LoginResponse(accessToken, refreshToken, message);
    }

    public static LoginResponse from(final String accessToken) {
        return LoginResponse.builder().accessToken(accessToken).build();
    }
}