package org.example.story.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.story.global.security.jwt.custom.CustomPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtHeaderFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            Claims claims = null;
            try {
                claims = jwtTokenProvider.getClaimsFromToken(token);
            } catch (ExpiredJwtException e) {
                log.debug("토큰이 만료되었습니다: {}", e.getMessage());
            } catch (SignatureException e) {
                log.debug("유효하지 않은 서명입니다: {}", e.getMessage());
            } catch (UnsupportedJwtException e) {
                log.debug("지원하지 않는 JWT입니다: {}", e.getMessage());
            } catch (MalformedJwtException e) {
                log.debug("토큰 형식이 잘못되었습니다: {}", e.getMessage());
            } catch (Exception e) {
                log.debug("JWT 처리 중 예상치 못한 오류: {}", e.getMessage());
            }
            if (claims != null) {
                try {
                    Long userId = claims.get("userId", Long.class);
                    String role = claims.get("role", String.class);

                    if (userId != null && role != null) {
                        CustomPrincipal principal = new CustomPrincipal(userId, role);

                        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                        Authentication auth = new UsernamePasswordAuthenticationToken(
                                principal, null, List.of(authority));

                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                } catch (ClassCastException | NumberFormatException e) {
                    log.debug("JWT claims 파싱 실패 : {}", e.getMessage());
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();

        if (uri.equals("/api/v1/auth/sign")
                || uri.startsWith("/api/v1/auth/login")
                || uri.startsWith("/api/v1/auth/verify")
                || uri.startsWith("/api/v1/auth/google")
                || uri.startsWith("/api/v1/auth/kakao")) {
            return true;
        } else if (request.getMethod().equals("GET") && (
                uri.startsWith("/api/v1/main/")
                        || uri.startsWith("/api/v1/profile/")
                        || uri.startsWith("/api/v1/portfolio/view/")
                        || uri.startsWith("/api/v1/portfolio/comment/")
                        || uri.startsWith("/api/v1/blog/view/")
                        || uri.startsWith("/api/v1/blog/comment/"))) {
            return true;
        }

        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        return false;
    }
}
