package com.github.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class UserContextFilter extends AbstractGatewayFilterFactory<UserContextFilter.Config> {
    public UserContextFilter() {
        super(Config.class);
    }

    public static class Config {
        // Put the configuration properties here if needed
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            return ReactiveSecurityContextHolder.getContext()
                    .map(securityContext -> {
                        // Authentication 객체에서 UserDetails를 가져옵니다.
                        Authentication authentication = securityContext.getAuthentication();
                        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                            String email = userDetails.getUsername();
                            log.info("게이트웨이 필터: " + email);
                            exchange.getRequest().mutate().header("X-User-Email", email).build();
                        }
                        return exchange;
                    })
                    .defaultIfEmpty(exchange) //인증 정보가 없어도 계속 exchange 진행
                    .flatMap(chain::filter);  // Proceed with the modified or original exchange
        };
    }
}