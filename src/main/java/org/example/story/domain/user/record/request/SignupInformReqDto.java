package org.example.story.domain.user.record.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SignupInformReqDto(
        @NotNull String nickname,
        @NotNull @Pattern(regexp = "^[0-9]{4}$") String studentId,
        @NotNull String major,
        String introduce,
        String profileImage
) {
}
