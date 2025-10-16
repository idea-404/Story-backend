package org.example.story.domain.portfolio.record.response;

import java.time.Instant;

public record PortfolioCommentResponse (
        // 이름이 곧 역할인 record
        Long id,
        Long portfolioId,
        Long userId,
        String content,
        Instant createdAt
){

}
