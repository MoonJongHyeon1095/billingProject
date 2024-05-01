package com.github.controller.response;

import com.github.domain.User;
import lombok.Getter;

@Getter
public class SignupResponse {
    private final String email;
    private final String message;

    public SignupResponse(final String email, final String message) {
        this.email = email;
        this.message = message;
    }

    public static SignupResponse from(final User savedUser, final String message) {
        return new SignupResponse(savedUser.getEmail(),message);
    }
}
