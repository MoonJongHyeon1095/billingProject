package com.github.config;

import com.github.filter.UserContextFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class GatewayConfig {
    private final UserContextFilter userContextFilter;

    public GatewayConfig(UserContextFilter userContextFilter) {
        this.userContextFilter = userContextFilter;
    }

    @Bean
    public RouteLocator gatewayRoutes (RouteLocatorBuilder builder){
        return builder.routes()
                .route(r -> r.path("/v1/user/**")
                        .uri("http://localhost:8081"))
                .route(r -> r.path("/v1/video/**")
                        //X-User-Email or X-Device-UUID
                        .filters(f -> f.filter(userContextFilter.apply(new UserContextFilter.Config())))
                        .uri("http://localhost:8082"))
                //정산내역 조회는 로그인 필요
                .route(r-> r.path("/v1/info/bill/**")
                        //X-User-Email
                        .filters(f -> f.filter(userContextFilter.apply(new UserContextFilter.Config()))
                        )
                        .uri("http://localhost:8084"))
                //top5 조회는 로그인 없이 가능
                .route(r-> r.path("/v1/info/top5/**")
                        .uri("http://localhost:8084"))
                .build();
    }

//    @Bean
//    public RouteLocator gatewayRoutes (RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route(r -> r.path("/v1/user/**")
//                        .uri("http://user_module:8081"))
//                .route(r -> r.path("/v1/video/**")
//                        .uri("http://video_module:8082"))
//                .route(r -> r.path("/v1/advertisement/**")
//                        .uri("http://advertisement_module:8083"))
//                .build();
//    }
}
