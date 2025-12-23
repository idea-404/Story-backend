package org.example.story.domain.profile.record.common;

import org.example.story.domain.portfolio.entity.PortfolioJpaEntity;

import java.time.Instant;

public record PortfolioCommonDto(
        Long id,
        String nickname,
        String title,
        String introduce,
        String content,
        Long like,
        Long view,
        Long comment,
        Instant createdAt
) {
    public PortfolioCommonDto(PortfolioJpaEntity e) {
        this(
                e.getId(),
                e.getUser().getNickname(),
                e.getTitle(),
                e.getIntroduce(),
                e.getContent(),
                e.getLike(),
                e.getView(),
                e.getComment(),
                e.getCreatedAt()
        );
    }
}
