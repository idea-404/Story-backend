package org.example.story.domain.user.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.example.story.domain.user.record.common.TokenDto;
import org.example.story.global.security.jwt.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class VerifyService {
    private final JwtTokenProvider jwtTokenProvider;
    private final GetAccountTokenService getAccountTokenService;

    public TokenDto execute(String token) {
        Claims claims = jwtTokenProvider.getClaimsFromToken(token);
        String email = claims.getSubject();
        return new TokenDto(getAccountTokenService.execute(email));
    }
}
