package org.example.story.domain.main.record;

import java.time.Instant;

public record BlogViewsResponse(
        Long id,
        Long userId,
        String nickname,
        String title,
        String content,
        Long like,
        Long view,
        Long comment,
        Instant createdAt,
        String thumbnail
) {
}
