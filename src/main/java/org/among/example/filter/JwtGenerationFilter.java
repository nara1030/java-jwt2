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
        // 로그인 사용자에 대해 JWT 토큰 발급
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            String token = jwtService.generateToken(authentication); // JWT 생성
            response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token); // 응답 헤더에 토큰 추가
        }
        System.out.println("Authentication Token in JwtGenerationFilter: ");
        System.out.println(response.getHeader(HttpHeaders.AUTHORIZATION));

        filterChain.doFilter(request, response);
    }
}
