package com.github.config;

import com.github.filter.JwtAuthenticationFilter;
import com.github.util.AuthenticationEntryPointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 SpringSecurity 설정

 - csrf().disable() : Cross-Site Request Forgery 보호.
 - SessionCreationPolicy.STATELESS : 세션을 사용하지 않는 상태. 각 요청을 독립적으로 처리하고 세션 상태를 서버에 저장하지 않음. (JWT 기반이니까)

 - BCryptPasswordEncoder의 인스턴스를 생성하여 반환
 */
@Configuration
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationEntryPointImpl authenticationEntryPoint;

    public SecurityConfig(
            final JwtAuthenticationFilter jwtAuthenticationFilter,
            final AuthenticationEntryPointImpl authenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }


    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests( auth -> auth
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/user/**").permitAll()
                        .anyRequest().authenticated())
                //첫번째 인자로 받은 필터를 두번째 인자로 받은 필터 이전에 추가. 인증 과정에서 사용자의 요청이 처리되기 전에 JWT를 확인하여 인증을 수행하
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                //Security 인증 에러시, 특정 엔트리 포인트로 가도록 설정
                .exceptionHandling(handle -> handle.authenticationEntryPoint(authenticationEntryPoint))
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
