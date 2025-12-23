package org.example.story.global.security.jwt.record.common;

import jakarta.validation.constraints.NotNull;

public record TokenClaimsDto(
        @NotNull String email,
        Long userId,
        String nickname,
        String studentId,
        String profileImage,
        String major,
        String introduce,
        String role
) {
}
