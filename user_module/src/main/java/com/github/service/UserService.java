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
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        final User user = User.builder()
                .email(userDto.getEmail())
                .password(encodedPassword)
                .build();
        System.out.println(userDto.getEmail());

        userMapper.insertUser(user);
    }

    private User findUserByEmail(final String email){
        return userMapper.findUserByEmail(email)
                .orElseThrow(() -> new UserException.UserNotFoundException(UserErrorCode.USER_NOT_FOUND));
    }

    private void validatePassword(final String password, final User user){
        boolean isValid = passwordEncoder.matches(password, user.getPassword());
        if(!isValid) throw new UserException.InvalidPasswordException(UserErrorCode.INVALID_PASSWORD_ERROR);
    }
}
