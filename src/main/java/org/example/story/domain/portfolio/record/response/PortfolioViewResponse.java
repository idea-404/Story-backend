package org.example.story.domain.portfolio.record.response;

import java.time.Instant;

public record PortfolioViewResponse(
        // 공통으로 사용되는 응답 record
        Long id,
        String nickname,
        String studentNumber,
        String title,
        String introduce,
        String content,
        Long like,
        Long view,
        Long comment,
        Boolean zerodog,
        Instant createdAt
){}
