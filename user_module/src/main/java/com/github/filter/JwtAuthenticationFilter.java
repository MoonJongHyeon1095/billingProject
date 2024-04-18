package com.github.filter;

import com.github.util.JwtTokenProvider;
import com.github.util.UserDetailServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 OncePerRequestFilter :
 Spring Security에서 제공하는 필터의 한 종류.
 서블릿 필터 체인 중 한 번만 실행되는 보장이 필요한 필터 작업을 쉽게 구현

 doFilterInternal :
 모든 HTTP 요청이 실제 처리되기 전에 호출.
 주로 요청 헤더나 쿠키 내의 토큰 같은 인증 정보를 검사하고 처리하는 메서드.

 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    //private final UserDetailsService userDetailsService;
    private final UserDetailServiceImpl userDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailServiceImpl userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        try{
            final String authorizationHeader = request.getHeader("Authorization");
            final String token = parseBearerToken(authorizationHeader);
            final String email = jwtTokenProvider.getEmail(token);
            final UserDetails userDetails = userDetailsService.loadUserByUsername(email);

//            Authentication auth = jwtTokenProvider.getAuthentication(token);
//            SecurityContextHolder.getContext().setAuthentication(auth);

            /**
             WebAutheticationDetails :
             추가 세부정보를 Authetication에 추가하는 객체
             주로 클라이언트 IP주소나 세션ID 포함

             WebAuthenticationDetailsSource().buildDetails(request):
             요청에서 WebAuthenticationDetails 객체를 생성. 이 객체는 요청에서 IP 주소와 세션 ID를 추출하여 저장.
             */
            final AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    token,
                    userDetails.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("jwt 인증 필터 통과");

        }catch(RuntimeException e){
            log.error("JWT 에러 {}", e.toString());
        } finally {
            filterChain.doFilter(request, response);
        }
    }

    private String parseBearerToken(final String authorizationHeader) {

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
