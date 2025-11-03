package org.example.story.global.security.auth;

import lombok.RequiredArgsConstructor;
import org.example.story.global.error.exception.ExpectedException;
import org.example.story.global.security.jwt.custom.CustomPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtils {
    public CustomPrincipal getCurrentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null
                || auth instanceof AnonymousAuthenticationToken
                || !auth.isAuthenticated()) {
            throw new ExpectedException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        if(auth.getPrincipal() instanceof CustomPrincipal) {
            return (CustomPrincipal) auth.getPrincipal();
        }

        throw new ExpectedException(HttpStatus.UNAUTHORIZED, "인증 정보가 올바르지 않습니다.");
    }

    public Long getCurrentUserId() {
        return getCurrentPrincipal().userId();
    }

    public String getCurrentRole() {
        return getCurrentPrincipal().role();
    }
}
