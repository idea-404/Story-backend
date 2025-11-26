package org.example.story.domain.image.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.story.domain.blog.entity.BlogJpaEntity;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Entity
@Table(name = "blog_image")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogImageJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "blog_id", nullable = false)
    private BlogJpaEntity blog;

    @Column(name = "imageUrl", nullable = false, length = 500)
    private String imageUrl;

}