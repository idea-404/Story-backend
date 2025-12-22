package org.example.story.domain.portfolio.record.common;

public record PortfolioRequest (
        // 공통으로 사용되는 요청 record
        String title,
        String content,
        String introduce
){}
