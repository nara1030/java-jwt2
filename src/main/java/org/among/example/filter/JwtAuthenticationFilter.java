package org.among.example.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.among.example.token.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);
        System.out.println("Authentication Token in JwtAuthenticationFilter: ");
        System.out.println(token);

        if (token != null && jwtService.validateToken(token)) {
            // JWT 토큰이 유효하면 인증 정보를 SecurityContext에 설정
            Authentication authentication = jwtService.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 토큰이 유효하면 로그인 필터(UsernamePasswordAuthenticationFilter) 및 토큰 생성 필터 실행 방지 필요(스프링 시큐리티 기본 로그인 사용 시)
            // 단, return문 사용 시 컨트롤러(UserController) 안 타서 해당 필터에 분기 사용
            // 시큐리티 로그인 사용: JwtAutehnticationFilter > UsernamePasswordAuthenticationFilter > JwtGenerationFilter
            // 커스텀 로그인 사용: JwtAutehnticationFilter > JwtGenerationFilter > LoginController
//            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // "Bearer " 제거하고 토큰 추출
        }
        return null;
    }
}
