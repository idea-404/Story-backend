package org.example.story.domain.blog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.story.domain.user.entity.UserJpaEntity;

@Getter
@Entity
@Table(name = "blog_like")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogLikeJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blog_id", nullable = false)
    private BlogJpaEntity blog;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

}