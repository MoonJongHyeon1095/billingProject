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
                //인증 인가 필요 X
                .route(r -> r.path("/v1/user/**")
                        //.uri("http://localhost:8081"))
                        .uri("lb://USER"))

                //시청기록 생성 auth 필터 거쳐야 함 (로그인 하지 않아도 괜찮지만, Email 값으로 분기처리를 함)
                .route(r -> r.path("/v1/video/stop")
                        //X-User-Email or X-Device-UUID
                        .filters(f -> f.filter(userContextFilter.apply(new UserContextFilter.Config())))
                        .uri("lb://WATCH-HISTORY"))
                        //.uri("http://localhost:8085"))

                //영상조회 auth 필터 거쳐야 함 (로그인 하지 않아도 괜찮지만, Email 값으로 분기처리를 함)
                .route(r -> r.path("/v1/video/play")
                        //X-User-Email or X-Device-UUID
                        .filters(f -> f.filter(userContextFilter.apply(new UserContextFilter.Config())))
                        .uri("http://localhost:8082"))
                        //.uri("lb://VIDEO"))

                //정산내역 조회는 인증 인가 필요
                .route(r-> r.path("/v1/info/bill/**")
                        //X-User-Email
                        .filters(f -> f.filter(userContextFilter.apply(new UserContextFilter.Config()))
                        )
                        .uri("http://localhost:8084"))
                        //.uri("lb://VIDEO-INFO"))
                //top5 조회는 인증 인가 필요 X
                .route(r-> r.path("/v1/info/top5/**")
                        .uri("http://localhost:8084"))
                        //.uri("lb://VIDEO-INFO"))
                .build();
    }
}
