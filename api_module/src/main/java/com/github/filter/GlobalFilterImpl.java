//package com.github.filter;
//
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//@Component
//public class GlobalFilterImpl implements GlobalFilter, Ordered {
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        return ReactiveSecurityContextHolder.getContext()
//                .map(securityContext -> {
//                    // Authentication 객체에서 UserDetails를 가져옵니다.
//                    Authentication authentication = securityContext.getAuthentication();
//                    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
//                        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//                        String email = userDetails.getUsername();
//                        // Add the email to the request header
//                        exchange.getRequest().mutate().header("X-User-Email", email).build();
//                    }
//                    return exchange;
//                })
//                .defaultIfEmpty(exchange)
//                .flatMap(chain::filter);  // Proceed with the modified or original exchange
//    }
//
//    @Override
//    public int getOrder() {
//        // 필터의 실행 순서를 정합니다. 숫자가 낮을수록 우선순위가 높습니다.
//        // 필요에 따라 순서를 조정해주세요.
//        return -1;
//    }
//}
