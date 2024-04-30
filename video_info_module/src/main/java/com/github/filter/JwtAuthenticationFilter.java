package com.github.filter;

import com.github.util.JwtTokenProvider;
import com.github.util.UserDetailServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final UserDetailServiceImpl userDetailsService;

    public JwtAuthenticationFilter(UserDetailServiceImpl userDetailsService, JwtTokenProvider jwtTokenProvider) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
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
            final Claims claims = jwtTokenProvider.extractClaims(token);
            final String email = jwtTokenProvider.getEmail(claims);

            // 토큰 만료 여부 검사
            if (token != null && jwtTokenProvider.isExpired(token)) {
                throw new ExpiredJwtException(null, null, "토큰이 만료되었습니다. 다시 로그인해주세요.");
            }

            final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            AbstractAuthenticationToken authentication =  new UsernamePasswordAuthenticationToken(
                    userDetails, token, userDetails.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("액세스 토큰으로 인증 처리됨");

        } catch (SecurityException | MalformedJwtException e){
            log.error(" JWT가 잘못 서명되었거나 변조된 경우. {}", e.toString());
            request.setAttribute("JwtAuthenticationFilterExceiption", e);
        } catch (ExpiredJwtException e){
            log.error("토큰 만료되었습니다. 다시 로그인 해주세요. {}", e.toString());
            request.setAttribute("JwtAuthenticationFilterExceiption", e);
        } catch ( UnsupportedJwtException e){
            log.error("지원되지 않는 토큰 형식입니다.{}", e.toString());
            request.setAttribute("JwtAuthenticationFilterExceiption", e);
        } catch ( IllegalArgumentException e){
            log.error("토큰 형식이 잘못되었습니다.{}", e.toString());
            request.setAttribute("JwtAuthenticationFilterExceiption", e);
        }  catch ( NullPointerException e ){
            log.error("요청에 토큰이 누락되었습니다. {}", e.toString());
            request.setAttribute("JwtAuthenticationFilterExceiption", e);
        } catch (RuntimeException e){
            log.error("기타 다른 JWT 에러 {}", e.toString());
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
