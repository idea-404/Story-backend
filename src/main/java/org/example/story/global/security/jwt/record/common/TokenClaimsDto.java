package org.example.story.global.security.jwt.record.common;

import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public record TokenClaimsDto(
        @NotNull String email,
        Long userId,
        String nickname,
        Long studentId,
        String profileImage,
        String major,
        String introduce,
        String role
) {
}
