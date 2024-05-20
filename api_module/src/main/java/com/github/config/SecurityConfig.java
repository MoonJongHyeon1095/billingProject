package com.github.config;

import com.github.filter.JwtAuthenticationFilter;
import com.github.filter.OAuth2LoginSuccessHandler;
import com.github.util.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 SpringSecurity 설정
 - Authentication 인증 : 사용자의 정보를 확인
 - Authorization 인가 : 그 사용자가 리소스에 접근할 권한
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    public SecurityConfig(
            final JwtAuthenticationFilter jwtAuthenticationFilter, final CustomAuthenticationEntryPoint authenticationEntryPoint, OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) //WWW-Authenticate 비활성화, jwt사용
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) //session STATELESS
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/v1/user/**").permitAll()
                        .pathMatchers("/v1/video/**").permitAll()
                        .pathMatchers("/v1/info/top5/**").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        .pathMatchers("/oauth2/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2.authenticationSuccessHandler(oAuth2LoginSuccessHandler))
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .exceptionHandling(handle -> handle.authenticationEntryPoint(authenticationEntryPoint));

        return http.build();
    }

}
