package org.example.story.domain.user.record.request;

import jakarta.validation.constraints.NotNull;

public record SignupInformReqDto(
        @NotNull String nickname,
        @NotNull Integer studentId,
        @NotNull String major,
        @NotNull String introduce,
        @NotNull String profileImage
) {
}
