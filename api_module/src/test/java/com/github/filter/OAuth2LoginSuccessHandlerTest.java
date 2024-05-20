package com.github.filter;

import com.github.util.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.WebFilterExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OAuth2LoginSuccessHandlerTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private WebFilterExchange webFilterExchange;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private OAuth2AuthenticationToken authentication;

    @Mock
    private OAuth2User oAuth2User;

    @InjectMocks
    private OAuth2LoginSuccessHandler successHandler;

    private static final String EMAIL = "test@example.com";
    private static final String ACCESS_TOKEN = "access_token";

    @BeforeEach
    void setUp() {
        when(webFilterExchange.getExchange().getResponse()).thenReturn(response);
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getAttribute("email")).thenReturn(EMAIL);
        when(jwtTokenProvider.createAccessToken(EMAIL)).thenReturn(ACCESS_TOKEN);
    }

    @Test
    void testOnAuthenticationSuccess() {
        HttpHeaders headers = new HttpHeaders();
        when(response.getHeaders()).thenReturn(headers);
        when(response.setComplete()).thenReturn(Mono.empty());

        Mono<Void> result = successHandler.onAuthenticationSuccess(webFilterExchange, authentication);
        result.block();

        assertEquals(ACCESS_TOKEN, headers.getFirst("Authorization"));
        verify(response, times(1)).setComplete();
    }
}