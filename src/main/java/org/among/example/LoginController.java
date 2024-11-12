package org.among.example;

import jakarta.servlet.http.HttpServletResponse;
import org.among.example.login.LoginRequest;
import org.among.example.token.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/login")
    public String login(Authentication authentication) {
        // 이미 로그인되어 있는 경우
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:page/home";
        }

        return "page/login";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        // 토큰 존재 시 로그인 안 타도록 하고 처리 필요
        // 기존 JwtAuthenticationFilter에서 처리하면 컨트롤러 안 타는 이슈
        // ...

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // 인증 성공 시 JWT 토큰 생성 및 응답 헤더 추가
        String token = jwtService.generateToken(authentication);
        httpServletResponse.setHeader("Authorization", "Bearer " + token);

        // 응답코드 설정
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);

        return ResponseEntity.ok().body("Custom Login Success");
    }

    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        System.out.println("로그아웃 컨트롤러");

        // 토큰 유효한 경우 로그아웃 처리
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = jwtService.getUsername(token);
        if (username == null || !jwtService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok().build();
    }
}
