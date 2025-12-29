package org.example.story.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.story.domain.user.record.common.TokenResDto;
import org.example.story.global.error.exception.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuthJwtService {
    private final GetAccountTokenService getAccountTokenService;

    public TokenResDto execute(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ExpectedException(HttpStatus.UNAUTHORIZED, "OAuth 인증 정보 없음");
        }

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        return getAccountTokenService.execute(email);
    }
}

