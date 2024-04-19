package com.github.controller.response;

import com.github.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SignupResponse {
    private final int userId;
    private final String email;
    private final String message;

    public SignupResponse(final int userId,final String email, final String message) {
        this.userId = userId;
        this.email = email;
        this.message = message;
    }

    public static SignupResponse from(final User savedUser, final String message) {
        return new SignupResponse(savedUser.getUserId(), savedUser.getEmail(),message);
    }
}
