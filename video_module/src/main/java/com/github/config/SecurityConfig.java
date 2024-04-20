//package com.github.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
///**
// SpringSecurity 설정
//
// - csrf().disable() : Cross-Site Request Forgery 보호.
// - SessionCreationPolicy.STATELESS : 세션을 사용하지 않는 상태. 각 요청을 독립적으로 처리하고 세션 상태를 서버에 저장하지 않음. (JWT 기반이니까)
//
// - BCryptPasswordEncoder의 인스턴스를 생성하여 반환
// */
//@Configuration
//public class SecurityConfig {
//
//
//    @Bean
//    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
//        return http
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(sessionManagement -> sessionManagement
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests( auth -> auth
//                        .requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/v1/video/**").permitAll()
//                        .anyRequest().authenticated())
//                .build();
//    }
//
//    @Bean
//    public BCryptPasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
//}
