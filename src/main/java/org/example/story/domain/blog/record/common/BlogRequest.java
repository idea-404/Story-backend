package org.example.story.domain.blog.record.common;

public record BlogRequest(
        // 공통으로 사용되는 요청 record
        String title,
        String content,
        String thumbnail
){}
