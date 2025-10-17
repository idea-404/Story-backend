package org.example.story.global.security.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final Key key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 토큰 생성 로직
    public String createToken(Long userId, Long expireTime) {
        Date now = new Date();
        Date expiredAt = new Date(now.getTime() + expireTime * 60000);
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        if(token == null) {
            throw new NullPointerException("토큰이 없습니다.");
        }
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 토큰에서 유저 ID 파싱
    public Long getUserIdFromToken(String token) {
        if(validateToken(token)) {
            return Long.valueOf(Jwts.parserBuilder().setSigningKey(key)
                    .build().parseClaimsJws(token).getBody().getSubject());
        }
        return null;
    }
}