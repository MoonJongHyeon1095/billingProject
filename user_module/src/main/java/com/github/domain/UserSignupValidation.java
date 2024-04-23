package com.github.domain;

import com.github.exception.UserErrorCode;
import com.github.exception.UserException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignupValidation {
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 20;
    //(.+) : 하나 이상의 어떤 문자든지 포함하는 하나의 그룹 //1.아무 문자(숫자포함)나 1개 이상 @ 앞뒤로 있을 것 2.반드시 @가 중간에 있을 것
    private static final String EMAIL_PATTERN = "^(.+)@(.+)$";
    private String email;
    private String password;

    @Builder
    public UserSignupValidation(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void validatePassword(){
        if(this.password.length() < MIN_PASSWORD_LENGTH || this.password.length() > MAX_PASSWORD_LENGTH )
        {
            throw new UserException.InvalidPasswordException(UserErrorCode.INVALID_PASSWORD_ERROR);
        }
    }

    public void validateEmail() {
        if (!email.matches(EMAIL_PATTERN)) {
            throw new UserException.InvalidEmailException(UserErrorCode.INVALID_EMAIL_ERROR);
        }
    }


}
