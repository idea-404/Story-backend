package org.example.story.domain.blog.record.response;

import java.time.Instant;

public record BlogCommentViewResponse(
        // 이름이 곧 역할인 record
        Long id,
        Long blogId,
        String nickname,
        String studentNumber,
        String content,
        Instant createdAt
){

}
