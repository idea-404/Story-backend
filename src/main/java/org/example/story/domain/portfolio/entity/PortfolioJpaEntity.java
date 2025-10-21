package org.example.story.domain.portfolio.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.story.domain.user.entity.UserJpaEntity;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "portfolio")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @Column(name = "title", nullable = false, length = 12)
    private String title;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @ColumnDefault("0")
    @Column(name = "`like`")
    private Long like;

    @ColumnDefault("0")
    @Column(name = "view")
    private Long view;

    @ColumnDefault("0")
    @Column(name = "zerodog")
    private Boolean zerodog;

    @Column(name = "createdAt", nullable = false)
    private Instant createdAt;

    @ColumnDefault("0")
    @Column(name = "comment")
    private Long comment;

}