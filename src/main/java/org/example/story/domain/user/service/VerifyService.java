package org.example.story.domain.user.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.example.story.domain.user.record.common.TokenDto;
import org.example.story.global.error.exception.ExpectedException;
import org.example.story.global.security.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class VerifyService {
    private final JwtTokenProvider jwtTokenProvider;
    private final GetAccountTokenService getAccountTokenService;

    public TokenDto execute(String token) {
        try {
            Claims claims = jwtTokenProvider.getClaimsFromToken(token);
            String email = claims.getSubject();
            return new TokenDto(getAccountTokenService.execute(email));
        } catch (ExpiredJwtException e) {
            throw new ExpectedException(HttpStatus.UNAUTHORIZED, "JWT 토큰이 만료되었습니다.");
        } catch (SignatureException e) {
            throw new ExpectedException(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 서명입니다.");
        } catch (UnsupportedJwtException e) {
            throw new ExpectedException(HttpStatus.UNAUTHORIZED, "지원하지 않는 JWT입니다.");
        } catch (Exception e) {
            throw new ExpectedException(HttpStatus.INTERNAL_SERVER_ERROR, "JWT 처리 중 예상치 못한 오류가 발생하였습니다.");
        }
    }
}
