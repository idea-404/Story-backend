package org.example.story.global.security.jwt.custom;

public record CustomPrincipal(
        Long userId,
        String role
) {
}
