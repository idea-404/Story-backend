package org.example.story.domain.portfolio.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.story.domain.user.entity.UserJpaEntity;

import java.time.Instant;

@Getter
@Entity
@Table(name = "portfolio_comment")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioCommentJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private PortfolioJpaEntity portfolio;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @Column(name = "content", nullable = false, length = 512)
    private String content;

    @Column(name = "createdAt")
    private Instant createdAt;
}