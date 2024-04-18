package com.github.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    //@Email(message = "Invalid email format")
    private String email;
    private String password;
}
