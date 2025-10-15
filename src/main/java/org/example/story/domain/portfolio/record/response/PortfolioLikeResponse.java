package org.example.story.domain.portfolio.record.response;

import java.time.Instant;

public record PortfolioLikeResponse (
        // 이름이 곧 역할인 record
        Long id,
        Long userId,
        String title,
        String content,
        Long like,
        Long view,
        Long comment,
        Boolean zerodog,
        Instant createdAt,
        Boolean liked
){

}
