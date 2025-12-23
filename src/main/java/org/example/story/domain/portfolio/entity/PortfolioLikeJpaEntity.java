package org.example.story.domain.portfolio.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.story.domain.user.entity.UserJpaEntity;

@Getter
@Entity
@Table(name = "portfolio_like")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioLikeJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private PortfolioJpaEntity portfolio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

}