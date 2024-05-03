package com.github.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.common.exception.ErrorResponse;
import com.github.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        return Mono.defer(() -> {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            final Response<ErrorResponse> body = Response.error(ErrorResponse.builder()
                    .status(HttpStatus.UNAUTHORIZED)
                    .message("[ERROR] 인증(autentication) 실패. 토큰을 제대로 넣어보십시오.")
                    .timestamp(LocalDateTime.now())
                    .build());

            byte[] bytes;
            try {
                bytes = objectMapper.writeValueAsBytes(body);
            } catch (Exception ex) {
                bytes = ("{\"error\": \"" + ex.getMessage() + "\"}").getBytes(StandardCharsets.UTF_8);
            }
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        });
    }
}
