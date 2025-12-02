package org.example.story.domain.main.record;

import java.time.Instant;

public record PortfolioViewResponse(
        Long id,
        Long userId,
        String title,
        String content,
        Long like,
        Long view,
        Long comment,
        Instant createdAt
) {
}
