package org.example.story.global.security.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.story.global.error.exception.ExpectedException;
import org.example.story.global.security.jwt.custom.CustomPrincipal;
import org.springframework.http.HttpStatus;
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
public class JwtHeaderFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String bearerToken = request.getHeader("Authorization");
        String token = null;
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                try {
                    Claims claims = jwtTokenProvider.getClaims(token);
                    Long userId = Long.parseLong(claims.getSubject());
                    String role = claims.get("role", String.class);

                    if (role != null) {
                        CustomPrincipal principal = new CustomPrincipal(userId, role);

                        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                        Authentication auth = new UsernamePasswordAuthenticationToken(
                                principal, null, List.of(authority));

                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                } catch (ClassCastException | NumberFormatException e) {
                    throw new ExpectedException(HttpStatus.BAD_REQUEST, "잘못된 claim 형식입니다.");
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
