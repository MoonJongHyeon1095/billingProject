package com.github.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class UserDto {
    private String email;
    private String password;

    public UserDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
