package org.example.story.domain.portfolio.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.story.domain.ola.entity.OlaHistoryJpaEntity;
import org.example.story.domain.user.entity.UserJpaEntity;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
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

    @Column(name = "createdAt")
    private Instant createdAt;

    @ColumnDefault("0")
    @Column(name = "comment")
    private Long comment;

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PortfolioLikeJpaEntity> likes = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PortfolioCommentJpaEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<OlaHistoryJpaEntity> ola = new ArrayList<>();


    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void increaseLike() {
        this.like++;
    }

    public void decreaseLike() {this.like--;}

    public void changeZerodog() {
        this.zerodog = !zerodog;
    }

    public void increaseView(){this.view++;}


    public void increaseComment(){this.comment++;}

    public void decreaseComment(){this.comment--;}

}