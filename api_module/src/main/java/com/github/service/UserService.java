package com.github.service;

import com.github.controller.response.LoginResponse;
import com.github.domain.User;
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

    /**
     * @Transactional 어노테이션이 붙은 메서드 내에서 @Transactional(readOnly = true) 어노테이션이 붙은 메서드를 호출하는 것
     * 실제로 이러한 패턴은 일반적인 상황에서 자주 사용된다고 카더라.
     */
    @Transactional(readOnly = true)
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
