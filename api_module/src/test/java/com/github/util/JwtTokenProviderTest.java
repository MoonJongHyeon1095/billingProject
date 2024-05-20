package com.github.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtTokenProviderTest {
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private Environment env;

    private String secretKey = "7YWM7Iqk7Yq47JqpIGJhc2U2NCBlbmNvZGVkIHNlY3JleSBrZXk="; // 테스트용 base64 encoded secrey key
    private String accessTokenExpiredTime = "3600000"; // 1 hour
    private String refreshTokenExpiredTime = "86400000"; // 24 hours
    @BeforeEach
    public void setUp() {
        when(env.getProperty("security.jwt.token.secret-key")).thenReturn(secretKey);
        when(env.getProperty("security.jwt.token.access-expire")).thenReturn(accessTokenExpiredTime);
        when(env.getProperty("security.jwt.token.refresh-expire")).thenReturn(refreshTokenExpiredTime);
        jwtTokenProvider = new JwtTokenProvider(
                env.getProperty("security.jwt.token.secret-key"),
                env.getProperty("security.jwt.token.access-expire"),
                env.getProperty("security.jwt.token.refresh-expire")
        );
    }
    @Test
    public void testCreateAccessToken() {
        String email = "test@example.com";
        String accessToken = jwtTokenProvider.createAccessToken(email);

        assertNotNull(accessToken);
        assertTrue(accessToken.startsWith("Bearer "));
    }
    @Test
    public void testCreateRefreshToken() {
        String email = "test@example.com";
        String refreshToken = jwtTokenProvider.createRefreshToken(email);

        assertNotNull(refreshToken);
        assertTrue(refreshToken.startsWith("Bearer "));
    }
    @Test
    public void testExtractClaims() {
        String email = "test@example.com";
        String accessToken = jwtTokenProvider.createAccessToken(email);
        Claims claims = jwtTokenProvider.extractClaims(accessToken.replace("Bearer ", ""));

        assertEquals(email, jwtTokenProvider.getEmail(claims));
        assertEquals(TokenType.ACCESS.name(), jwtTokenProvider.getTokenType(claims));
    }
    @Test
    public void testIsExpired() {
        Claims claims = Jwts.claims().setExpiration(new Date(System.currentTimeMillis() - 1000));
        assertTrue(jwtTokenProvider.isExpired(claims));

        claims.setExpiration(new Date(System.currentTimeMillis() + 1000));
        assertFalse(jwtTokenProvider.isExpired(claims));
    }
    @Test
    public void testTokenValidation() {
        String email = "test@example.com";
        String accessToken = jwtTokenProvider.createAccessToken(email);

        Claims claims = jwtTokenProvider.extractClaims(accessToken.replace("Bearer ", ""));
        assertEquals(email, claims.get("email", String.class));
        assertEquals(TokenType.ACCESS.name(), claims.get("tokenType", String.class));
    }
}
