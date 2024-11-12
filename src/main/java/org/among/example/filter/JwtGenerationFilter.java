package org.among.example.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.among.example.token.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtGenerationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JwtGenerationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 유효한 토큰에 대해 로직 건너뛰기
        String token = getTokenFromRequest(request);
        if (shouldSkipFilter(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 로그인 사용자에 대해 JWT 토큰 발급
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            token = jwtService.generateToken(authentication); // JWT 생성
            response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token); // 응답 헤더에 토큰 추가

            System.out.println("Authentication Token in JwtGenerationFilter: ");
            System.out.println(response.getHeader(HttpHeaders.AUTHORIZATION));
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

    // 필터를 건너뛰어야 하는 조건을 검사하는 메소드
    private boolean shouldSkipFilter(String token) {
        return token != null && jwtService.validateToken(token);
    }
}
