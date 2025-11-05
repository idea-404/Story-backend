package org.example.story.global.config;

import lombok.RequiredArgsConstructor;
import org.example.story.global.error.exception.ExpectedException;
import org.example.story.global.security.jwt.JwtHeaderFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtHeaderFilter jwtHeaderFilter;

    // Spring Security 필터 체인 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 비활성화 (REST API에서는 일반적으로 사용하지 않음)
                .csrf(AbstractHttpConfigurer::disable)

                // CORS 설정 비활성화 (WebConfig에서 따로 설정했기 때문)
                .cors(AbstractHttpConfigurer::disable)

                // 기본 제공되는 로그인 폼 사용 안 함
                .formLogin(AbstractHttpConfigurer::disable)

                // HTTP Basic 인증 방식 비활성화 (JWT 방식 사용하기 위해 끔)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 세션을 사용하지 않고, 매 요청마다 토큰으로 인증 (STATELESS)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 요청에 대한 인가 규칙 설정
                .authorizeHttpRequests(auth -> auth
                        // 아래 경로들은 인증 없이 접근 허용
                        .requestMatchers(
                                // 인증/회원가입 관련
                                "/api/v1/auth/**",
                                "/api/v1/auth/login",
                                "/api/v1/auth/signup",
                                "/api/v1/auth/verify",
                                "/api/v1/auth/google",
                                "/api/v1/auth/kakao"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.GET, // 아래 모든 경로 GET 요청에만 허용
                                // 메인 화면
                                "/api/v1/main/**",

                                // 다른 유저 프로필 조회
                                "/api/v1/profile/*",

                                // 포트폴리오
                                "/api/v1/portfolio/view/**", // 포트폴리오 조회
                                "/api/v1/portfolio/comment/**", // 포트폴리오 댓글 조회

                                // 블로그
                                "/api/v1/blog/view/**", // 블로그 조회
                                "/api/v1/blog/comment/**" // 블로그 댓글 조회
                        ).permitAll()
                        // 나머지 모든 요청은 인증 필요
                        .anyRequest().hasAnyRole("VERIFIED", "ADMIN")
                )
                .addFilterBefore(jwtHeaderFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, exception) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"message\":\"로그인이 되어있지 않습니다.\"}");
                        })
                        .accessDeniedHandler((request, response, exception) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"message\":\"접근 권한이 없습니다.\"}");
                        }));

        return http.build();
    }

}
