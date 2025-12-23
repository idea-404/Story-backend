package org.example.story.domain.blog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.story.domain.portfolio.entity.PortfolioLikeJpaEntity;
import org.example.story.domain.user.entity.UserJpaEntity;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "blog")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "introduce", length = 500)
    private String introduce;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @ColumnDefault("0")
    @Column(name = "`like`")
    private Long like;

    @ColumnDefault("0")
    @Column(name = "view")
    private Long view;

    @Column(name = "createdAt")
    private Instant createdAt;

    @ColumnDefault("0")
    @Column(name = "comment")
    private Long comment;

    @Column(name = "thumbnail")
    private String thumbnail;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<BlogLikeJpaEntity> likes = new ArrayList<>();

    @OneToMany(mappedBy = "blog", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<BlogCommentJpaEntity> comments = new ArrayList<>();

    public void update(String title, String content, String introduce, String thumbnail) {
        this.title = title;
        this.content = content;
        this.introduce = introduce;
        this.thumbnail = thumbnail;
    }
    public void increaseLike() {
        this.like++;
    }
    public void decreaseLike() {this.like--;}
    public void increaseView(){this.view++;}
    public void increaseComment(){this.comment++;}
    public void decreaseComment(){this.comment--;}
}
