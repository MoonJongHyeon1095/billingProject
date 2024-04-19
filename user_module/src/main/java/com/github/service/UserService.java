package com.github.service;

import com.github.controller.response.LoginResponse;
import com.github.domain.User;
import com.github.domain.UserSignupValidation;
import com.github.dto.UserDto;
import com.github.exception.UserErrorCode;
import com.github.exception.UserException;
import com.github.mapper.UserMapper;
import com.github.util.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponse login(UserDto userDto) {
        final String email = userDto.getEmail();
        final String password = userDto.getPassword();

        final User user = findUserByEmail(email);
        validatePassword(password, user);

        final String accessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getEmail());

        return LoginResponse.from(accessToken);

    }

    @Transactional
    public void signup(UserDto userDto) {
        //이메일과 비밀번호 형식 검증
        UserSignupValidation validator = UserSignupValidation.builder()
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .build();
        validator.validateEmail();
        validator.validatePassword();

        //DB조회 이메일 중복검사
        validateDuplicatedEmail(userDto.getEmail());

        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        final User user = User.builder()
                .email(userDto.getEmail())
                .password(encodedPassword)
                .build();

        userMapper.insertUser(user);
    }

    private User findUserByEmail(final String email){
        return userMapper.findUserByEmail(email)
                .orElseThrow(() -> new UserException.UserNotFoundException(UserErrorCode.USER_NOT_FOUND));
    }

    private void validateDuplicatedEmail(final String email){
        userMapper.findUserByEmail(email)
                .ifPresent((present) -> {
                    throw new UserException.UserDuplicatedException(UserErrorCode.USER_DUPLICATED);
                });
    }

    private void validatePassword(final String password, final User user){
        boolean isValid = passwordEncoder.matches(password, user.getPassword());
        if(!isValid) throw new UserException.InvalidPasswordException(UserErrorCode.INVALID_PASSWORD_ERROR);
    }
}
