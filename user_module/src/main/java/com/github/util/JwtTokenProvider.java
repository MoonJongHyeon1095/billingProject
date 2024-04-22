package com.github.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtTokenProvider {
    private static final String BEARER_PREFIX = "Bearer ";

    private int accessTokenExpiredTime;

    private int refreshTokenExpiredTime;

    private byte[] keyBytes;


public JwtTokenProvider (
        @Value("${security.jwt.token.secret-key:secret-key}") String secretKey, //base64
        @Value("${security.jwt.token.access-expire}") String accessTokenExpiredTime,
        @Value("${security.jwt.token.refresh-expire}") String refreshTokenExpiredTime
        ) {
    this.keyBytes = Base64.getDecoder().decode(secretKey);
    this.accessTokenExpiredTime = Integer.parseInt(accessTokenExpiredTime);
    this.refreshTokenExpiredTime = Integer.parseInt(refreshTokenExpiredTime);
    }

    public String createAccessToken(int userId, String email){
    return createToken(userId, email, accessTokenExpiredTime, TokenType.ACCESS);
    }

    public String createRefreshToken(int userId, String email){
        return createToken(userId, email, refreshTokenExpiredTime, TokenType.REFRESH);
    }

    private String createToken(int userId, String email, int expire, TokenType tokenType) {
        Claims claims = Jwts.claims();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("tokenType", tokenType.name());

        Date now = new Date();
        Date expireTime = new Date(now.getTime() + expire);

        String token = Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(expireTime)
                    .signWith(getSigningkey(keyBytes))
                    .compact();
        return BEARER_PREFIX + token;
    }

    public boolean isExpired(String token){
        Date expiredDate = extractClaims(token).getExpiration();
        return expiredDate.before(new Date()); //이전 날짜면 true 반환
    }

    public String getEmail(Claims claims) {
        return claims.get("email", String.class);
    }

    public int getUserId(Claims claims) {
        return claims.get("userId", Integer.class);
    }

    public String getTokenType(Claims claims) {
        return claims.get("tokenType", String.class);
    }

    public Claims extractClaims(String token){
        return Jwts.parserBuilder().setSigningKey(getSigningkey(keyBytes))
                .build().parseClaimsJws(token).getBody();
    }

    public Claims extractClaimsFromRefreshToken(String token){
        return extractClaims(parseBearerToken(token));
    }

    private Key getSigningkey(byte[] keyBytes){
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String parseBearerToken(final String inputString) {

        if (inputString != null && inputString.startsWith("Bearer ")) {
            return inputString.substring(7);
        }
        return null;
    }
}
