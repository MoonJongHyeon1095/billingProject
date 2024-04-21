package com.github.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator gatewayRoutes (RouteLocatorBuilder builder){
        return builder.routes()
                .route(r -> r.path("/v1/user/**")
                        .uri("http://localhost:8081"))
                .route(r -> r.path("/v1/video/**")
                        //로그인 안한 사용자의 시청기록도 UUID로 관리
                        .filters(f -> f.addRequestHeader("X-Device-UUID", "DeviceUUID"))
                        .uri("http://localhost:8082"))
                .route(r -> r.path("/v1/advertisement/**")
                        .uri("http://localhost:8083"))
                .build();
    }
}
