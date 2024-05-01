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
    //고유한 email이 PK
    private String email;

    private String password;

    @Setter
    private String refreshToken;

    private Boolean role;
}
