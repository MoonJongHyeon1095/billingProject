package com.github.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter // 접근자는 직렬화 과정에서 Jackson이 객체의 필드에 쉽게 접근할 수 있도록 합니다.
@NoArgsConstructor //기본 생성자가 없으면 Jackson이 객체를 역직렬화할 때 문제가 발생할 수 있습니다.
@AllArgsConstructor // 모든 필드를 포함한 생성자는 JSON 직렬화/역직렬화에서 각 필드를 설정할 수 있는 방법을 제공
public class UserDto {
    private String email;
    private String password;
}
