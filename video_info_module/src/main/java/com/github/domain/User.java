package com.github.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Builder
public class User {
    //고유한 email이 PK
    private String email;
    private String password;
    @Setter
    private String refreshToken;
    private Boolean role;
}

