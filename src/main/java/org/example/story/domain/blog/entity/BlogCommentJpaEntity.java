package org.example.story.domain.blog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.story.domain.user.entity.UserJpaEntity;

import java.time.Instant;

@Getter
@Entity
@Table(name = "blog_comment")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogCommentJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blog_id", nullable = false)
    private BlogJpaEntity blog;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @Column(name = "content", nullable = false, length = 512)
    private String content;

    @Column(name = "createdAt", nullable = false)
    private Instant createdAt;

}