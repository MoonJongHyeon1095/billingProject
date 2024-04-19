package com.github.service;

import com.github.controller.response.RefreshResponse;
import com.github.domain.User;
import com.github.dto.RefreshDto;
import com.github.exception.AuthErrorCode;
import com.github.exception.AuthException;
import com.github.mapper.UserMapper;
import com.github.util.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;


    public RefreshResponse refresh(final RefreshDto refreshDto){
        final String refreshToken = refreshDto.getRefreshToken();
        final Claims claims = jwtTokenProvider.extractClaims(refreshToken);
        final int userId = jwtTokenProvider.getUserId(claims);
        final User user = findUserById(userId);
        validateSameToken(user.getRefreshToken(), refreshToken);

        final String newAccessToken = jwtTokenProvider.createAccessToken(user.getUserId(), user.getEmail());
        return RefreshResponse.from(newAccessToken, refreshToken);

    }

    private User findUserById(final int userId){
        return userMapper.findUserById(userId).orElseThrow(()-> new AuthException(AuthErrorCode.NOT_FOUND_REFRESH_TOKEN));
    }

    private void validateSameToken(final String foundToken, final String inputToken){
        if(!foundToken.equals(inputToken)) throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
    }
}
