package org.example.story.domain.blog.record.response;

import java.time.Instant;

public record BlogLikeResponse(
        // 이름이 곧 역할인 record
        Long id,
        Long userId,
        String title,
        String content,
        Long like,
        Long view,
        Long comment,
        Instant createdAt,
        Boolean liked
){

}
