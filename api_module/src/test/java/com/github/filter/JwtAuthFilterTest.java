package com.github.filter;

import com.github.util.JwtTokenProvider;
import com.github.util.UserDetailServiceImpl;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.net.URI;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthFilterTest {
    @Mock
    private UserDetailServiceImpl userDetailsService;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private ServerWebExchange exchange;
    @Mock
    private WebFilterChain chain;
    @Mock
    private ServerHttpRequest request;
    @Mock
    private ServerHttpResponse response;
    @Mock
    private Claims claims;
    @Mock
    private UserDetails userDetails;
    @InjectMocks
    private JwtAuthenticationFilter filter;

    private static final String BEARER_TOKEN = "Bearer valid.jwt.token";
    private static final String TOKEN = "valid.jwt.token";
    private static final String EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        lenient().when(exchange.getRequest()).thenReturn(request);
        lenient().when(exchange.getResponse()).thenReturn(response);
    }

    @Test
    void testFilterBypass() {
        when(request.getURI()).thenReturn(URI.create("/v1/user/login"));
        when(chain.filter(exchange)).thenReturn(Mono.empty());
        Mono<Void> result = filter.filter(exchange, chain);
        result.block();
        verify(chain, times(1)).filter(exchange);
        verify(jwtTokenProvider, times(0)).extractClaims(anyString());
    }

    @Test
    void testFilterValidToken() {
        when(request.getURI()).thenReturn(URI.create("/v1/protected/endpoint"));
        when(request.getHeaders()).thenReturn(new HttpHeaders() {{
            add(AUTHORIZATION, BEARER_TOKEN);
        }});
        when(jwtTokenProvider.extractClaims(TOKEN)).thenReturn(claims);
        when(jwtTokenProvider.isExpired(claims)).thenReturn(false);
        when(jwtTokenProvider.getEmail(claims)).thenReturn(EMAIL);
        when(userDetailsService.findByUsername(EMAIL)).thenReturn(Mono.just(userDetails));
        when(userDetails.getAuthorities()).thenReturn(new ArrayList<>());

        when(chain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = filter.filter(exchange, chain);
        result.contextWrite(Context.of(ReactiveSecurityContextHolder.withAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())))).block();

        verify(chain, times(1)).filter(exchange);
        verify(jwtTokenProvider, times(1)).extractClaims(TOKEN);
    }

    @Test
    void testFilterExpiredToken() {
        when(request.getURI()).thenReturn(URI.create("/v1/protected/endpoint"));
        when(request.getHeaders()).thenReturn(new HttpHeaders() {{
            add(AUTHORIZATION, BEARER_TOKEN);
        }});
        when(jwtTokenProvider.extractClaims(TOKEN)).thenReturn(claims);
        when(jwtTokenProvider.isExpired(claims)).thenReturn(true);

        when(response.setStatusCode(HttpStatus.UNAUTHORIZED)).thenReturn(true);
        when(response.setComplete()).thenReturn(Mono.empty());

        Mono<Void> result = filter.filter(exchange, chain);
        result.block();

        verify(response, times(1)).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(response, times(1)).setComplete();
    }

    @Test
    void testFilterNoToken() {
        when(request.getURI()).thenReturn(URI.create("/v1/protected/endpoint"));
        when(request.getHeaders()).thenReturn(new HttpHeaders());

        when(response.setStatusCode(HttpStatus.UNAUTHORIZED)).thenReturn(true);
        when(response.setComplete()).thenReturn(Mono.empty());

        Mono<Void> result = filter.filter(exchange, chain);
        result.block();

        verify(response, times(1)).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(response, times(1)).setComplete();
    }

    @Test
    void testFilterInvalidBearerToken() {
        when(request.getURI()).thenReturn(URI.create("/v1/protected/endpoint"));
        when(request.getHeaders()).thenReturn(new HttpHeaders() {{
            add(AUTHORIZATION, "InvalidBearerToken");
        }});

        when(response.setStatusCode(HttpStatus.UNAUTHORIZED)).thenReturn(true);
        when(response.setComplete()).thenReturn(Mono.empty());

        Mono<Void> result = filter.filter(exchange, chain);
        result.block();

        verify(response, times(1)).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(response, times(1)).setComplete();
    }
}
