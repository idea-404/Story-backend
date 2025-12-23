package org.example.story.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import org.example.story.global.error.exception.ExpectedException;
import org.example.story.global.security.jwt.record.common.TokenClaimsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final Key key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        this.key =
                Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // 토큰 생성 로직
    public String createToken(
            @Valid TokenClaimsDto claimsDto, Long expireTime) {
        Date now = new Date();
        Date expiredAt = new Date(now.getTime() + expireTime * 60000);
        return Jwts.builder()
                .setSubject(claimsDto.email())
                .claim("userId", claimsDto.userId())
                .claim("nickname", claimsDto.nickname())
                .claim("studentId", claimsDto.studentId())
                .claim("profileImage", claimsDto.profileImage())
                .claim("major", claimsDto.major())
                .claim("introduce", claimsDto.introduce())
                .claim("role", claimsDto.role())
                .setIssuedAt(now)
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public void validatedToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (JwtException e) {
            throw new ExpectedException(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다!");
        }
    }

    // 토큰 유효성 검사 후 권한 파싱
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                    .setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
