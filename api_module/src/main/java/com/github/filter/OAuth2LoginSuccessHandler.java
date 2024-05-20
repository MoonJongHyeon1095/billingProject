package com.github.filter;

import com.github.util.JwtTokenProvider;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class OAuth2LoginSuccessHandler implements ServerAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;

    public OAuth2LoginSuccessHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();

        String email = ((OAuth2AuthenticationToken) authentication).getPrincipal().getAttribute("email");
        String accessToken = jwtTokenProvider.createAccessToken(email);
        response.getHeaders().add("Authorization", accessToken);
        return response.setComplete();
    }
}
