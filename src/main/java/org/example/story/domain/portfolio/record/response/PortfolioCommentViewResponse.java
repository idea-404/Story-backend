package org.example.story.domain.portfolio.record.response;

import java.time.Instant;

public record PortfolioCommentViewResponse(
        // 이름이 곧 역할인 record
        Long id,
        Long portfolioId,
        String nickname,
        String studentNumber,
        String content,
        Instant createdAt
){

}
