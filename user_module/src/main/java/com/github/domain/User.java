package com.github.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class User {
    private Integer userId;  // Integer로 변경하여 null 가능하게 함

    @Setter
    private String email;

    @Setter
    private String password;
}
