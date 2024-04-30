package com.github.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.common.exception.ErrorResponse;
import com.github.common.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 스프링 시큐리티(Spring Security)의 커스텀 인증 진입점(Custom Authentication EntryPoint)을 구현

 - ObjectMapper: JSON 처리. ErrorResponse 자바 객체를 JSON 문자열로 직렬화하는 데 사용
 - commence : 필수 구현 메서. 인증 실패시 호출되는 메서드. 로그를 기록하고, 인증 실패 예외를 처리하여 사용자에게 JSON 형태의 에러 응답
 - createResponse : 실제 응답 생성
 */
@Slf4j
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    public AuthenticationEntryPointImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final AuthenticationException authException
    ) throws IOException {
        log.error("[ERROR] occurs CustomAuthenticationEntryPoint -> commence()");

        final Exception e = (Exception) request.getAttribute("JwtAuthenticationFilterException");
        createResponse(response, e);
    }

    private void createResponse(final HttpServletResponse response, final Exception e) throws IOException {
        final Response<ErrorResponse> body = Response.error(ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .message("[ERROR] Jwt 인증필터에서 토큰 인증에 실패했다는데요...")
                .timestamp(LocalDateTime.now())
                .build()
        );
        response.setContentType("application/json");
        response.setStatus(401);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
