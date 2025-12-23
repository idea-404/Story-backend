package org.example.story.domain.blog.record.common;

import java.time.Instant;

public record BlogResponse(
        // 공통으로 사용되는 응답 record
        Long id,
        String nickname,
        String title,
        String introduce,
        String content,
        Long like,
        Long view,
        Long comment,
        Instant createdAt
){}
