package org.example.story.domain.user.record.common;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record EmailDto(
        @NotNull @Email String email
) {
}
