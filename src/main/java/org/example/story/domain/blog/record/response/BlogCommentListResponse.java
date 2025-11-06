package org.example.story.domain.blog.record.response;

import java.util.List;

public record BlogCommentListResponse(
        Long blogId,
        List<BlogCommentResponse> comments
) { }
