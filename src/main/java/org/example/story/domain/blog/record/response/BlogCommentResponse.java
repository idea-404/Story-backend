package org.example.story.domain.blog.record.response;

import java.time.Instant;

public record BlogCommentResponse(
        // 이름이 곧 역할인 record
        Long id,
        Long blogId,
        Long userId,
        String content,
        Instant createdAt
){

}
