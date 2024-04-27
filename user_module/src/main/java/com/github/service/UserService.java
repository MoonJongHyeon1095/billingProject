package com.github.service;

import com.github.controller.response.LoginResponse;
import com.github.controller.response.SignupResponse;
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
    public LoginResponse login(final UserDto userDto) {
        final String email = userDto.getEmail();
        final String password = userDto.getPassword();

        //1.사용자가 존재하는지 검증
        final User user = findUserByEmail(email);
        //2.1에서 조회된 사용자의 비밀번호와 새로 입력된 비밀번호 대조
        validatePassword(password, user);

        final String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());
        final String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());
        user.setRefreshToken(refreshToken);
        userMapper.updateRefreshToken(user);

        return LoginResponse.from(accessToken, refreshToken, "로그인 성공!");

    }

    @Transactional
    public SignupResponse signup(final UserDto userDto) {
        //이메일과 비밀번호 형식 검증
        UserSignupValidation validator = UserSignupValidation.builder()
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .build();
        validator.validateEmail();
        validator.validatePassword();

        //DB조회 이메일 중복검사
        validateDuplicatedEmail(userDto.getEmail());

        //비밀번호 암호화 후 저장
        final String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        final User user = User.builder()
                .email(userDto.getEmail())
                .password(encodedPassword)
                .build();
        userMapper.insertUser(user);

        // 삽입된 사용자 정보 다시 조회 후 반환
        final User newUser = findUserByEmail(userDto.getEmail());

        return SignupResponse.from(newUser, "회원가입 성공!");
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
