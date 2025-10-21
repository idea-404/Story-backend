package org.example.story.global.config;

import lombok.RequiredArgsConstructor;
import org.example.story.global.security.jwt.JwtHeaderFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                                "/api/v1/auth/**",       // 로그인, 회원가입, 이메일 인증, 소셜 로그인 등
                                "/api/v1/auth/login",
                                "/api/v1/auth/signup",
                                "/api/v1/auth/verify",
                                "/api/v1/auth/google",
                                "/api/v1/auth/kakao",

                                // 프로필 (다른 유저 공개 프로필 조회)
                                "/api/v1/profile/**",

                                // 블로그 (공개 조회 관련)
                                "/api/v1/blog/list",
                                "/api/v1/blog/view/**",

                                // 포트폴리오 (공개 조회 관련)
                                "/api/v1/portfolio/list",
                                "/api/v1/portfolio/view/**",

                                // MyPage (자기정보 외엔 대부분 보호, 기본 루트 공개 가능 여부 선택)
                                "/api/v1/mypage/"
                        ).permitAll()

                        // 나머지 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtHeaderFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
