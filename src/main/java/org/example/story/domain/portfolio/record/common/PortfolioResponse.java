package org.example.story.domain.portfolio.record.common;

import java.time.Instant;

public record PortfolioResponse (
        // 공통으로 사용되는 응답 record
        Long id,
        Long userId,
        String title,
        String introduce,
        String content,
        Long like,
        Long view,
        Long comment,
        Boolean zerodog,
        Instant createdAt
){}
