package org.example.story.domain.profile.record.common;

import org.example.story.domain.blog.entity.BlogJpaEntity;

import java.time.Instant;

public record BlogCommonDto(
        Long id,
        String nickname,
        String title,
        String content,
        Long like,
        Long view,
        Long comment,
        Instant createdAt,
        String thumbnail
) {
    public BlogCommonDto(BlogJpaEntity b) {
        this(
                b.getId(),
                b.getUser().getNickname(),
                b.getTitle(),
                b.getContent(),
                b.getLike(),
                b.getView(),
                b.getComment(),
                b.getCreatedAt(),
                b.getThumbnail()
        );
    }
}
