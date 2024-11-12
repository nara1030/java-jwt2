package org.among.example.config;

import org.among.example.filter.JwtAuthenticationFilter;
import org.among.example.filter.JwtGenerationFilter;
import org.among.example.token.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private JwtService jwtService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable); // 커스텀 로그인 프로세스(POST) 403 방지
        http
                .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new JwtGenerationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);
        http
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/").permitAll() // 최초 페이지 모두 접근 가능하지만 노출 다르게 설정
                        .requestMatchers(HttpMethod.GET, "/login").permitAll() // 커스톰 로그인 페이지 접근 허용
                        .requestMatchers(HttpMethod.POST, "/login").permitAll() // 커스톰 로그인 프로세스 접근 허용
                        .requestMatchers(HttpMethod.POST, "/logout").permitAll() // 커스톰 로그아웃 프로세스 접근 허용
                        .requestMatchers(HttpMethod.GET, "/userinfo").permitAll() // TOP 메뉴 사용자 표시
                        .anyRequest().authenticated()
                )
//                .formLogin(Customizer.withDefaults()) // 스프링 제공 양식 사용
//                .logout(logout -> logout
//                        .logoutSuccessUrl("/")
//                )

//                .formLogin(formLogin -> formLogin // 토큰 테스트 위해 커스텀 로그인 사용하고자 했으나 스프링 시큐리티 필터가 가로챔..
//                        .loginPage("/login")
//                        .loginProcessingUrl("/login")
//                )

                .formLogin(AbstractHttpConfigurer::disable) // 폼 로그인 방지(컨트롤러 로그인 사용)
                .exceptionHandling(exceptions ->
                        exceptions.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")) // 인증 실패 시 커스텀 로그인 페이지로 리다이렉트
                )
                .logout(AbstractHttpConfigurer::disable); // 커스텀 로그아웃(기본 로그아웃 필터 미사용)

        return http.build();
    }

//    @Autowired
//    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
//
//    }

    // 어차피 UsernamePasswordAuthenticationFilter 사용하는 거라 Provider 재정의 불필요해 보이나 토큰 정보 확인 위해 작성
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider)
                .build();
    }

}
