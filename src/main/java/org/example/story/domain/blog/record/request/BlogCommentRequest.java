package org.example.story.domain.blog.record.request;

public record BlogCommentRequest(
        // 이름이 곧 역할인 record
        String content
){}
