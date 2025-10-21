package org.example.story.global.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.story.global.error.exception.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

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
        if(bearerToken == null) {
            throw new ExpectedException(HttpStatus.NOT_FOUND, "Bearer token이 없습니다.");
        }
        if(!bearerToken.startsWith("Bearer ")) {
            throw new ExpectedException(HttpStatus.UNAUTHORIZED, "Bearer 형식이 올바르지 않습니다.");
        }
        String token = null;
        token = bearerToken.substring(7);
        if(!jwtTokenProvider.validateToken(token)) {
            throw new ExpectedException(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다.");
        }
        request.setAttribute("token", token);
        filterChain.doFilter(request, response);
    }
}
