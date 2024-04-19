package com.github.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 - MyBatis를 사용할 때, 도메인 객체는 실제로 매우 간단하게 유지하는 것이 일반적
    1.데이터 매핑의 단순해진다.
    2.유지 보수의 용이함, 재사용에 유연함

 */
@Getter
@Builder
public class User {
    private Integer userId;  // Integer로 변경하여 null 가능하게 함

    private String email;

    private String password;

    private String refreshToken;
}
