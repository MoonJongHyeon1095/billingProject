package com.github.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtTokenProvider {
    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length:3600000}") // 1 hour
    private int expireLength;

    private byte[] keyBytes;


public JwtTokenProvider (
        @Value("${security.jwt.token.secret-key:secret-key}") String secretKey,
        @Value("${security.jwt.token.expire-length:3600000}") int expireLength ) {
    this.keyBytes = Base64.getDecoder().decode(secretKey);
    this.expireLength = expireLength;
}

    public String createAccessToken(int userId, String email) {
        Claims claims = Jwts.claims();
        claims.put("userId", userId);
        claims.put("email", email);

        Date now = new Date();
        Date expireTime = new Date(now.getTime() + expireLength);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireTime)
                .signWith(getSigningkey(keyBytes))
                .compact();
    }

    public boolean isExpired(String token){
        Date expiredDate = extractClaims(token).getExpiration();
        return expiredDate.before(new Date()); //이전 날짜면 true 반환
    }

    public String getEmail(String token) {
        return extractClaims(token).get("email", String.class);
    }

    public Long getUserId(String token) {
        return extractClaims(token).get("userId", Long.class);
    }

    private Claims extractClaims(String token){
//        if(tokenType.equals(TokenType.ACCESS)){
//            return Jwts.parserBuilder().setSigningKey(getSigningkey(keyBytes))
//                    .build().parseClaimsJws(token).getBody();
//        }
        return Jwts.parserBuilder().setSigningKey(getSigningkey(keyBytes))
                .build().parseClaimsJws(token).getBody();
    }

    private Key getSigningkey(byte[] keyBytes){
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
