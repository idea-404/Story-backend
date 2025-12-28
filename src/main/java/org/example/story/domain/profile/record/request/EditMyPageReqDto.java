package org.example.story.domain.profile.record.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record EditMyPageReqDto(
        @NotNull String nickname,
        @NotNull @Pattern(regexp = "^[0-9]{4}$") String studentId,
        @NotNull String major,
        @NotNull String introduce,
        String profileImage
) {
}
