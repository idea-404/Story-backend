package org.example.story.domain.image.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.story.domain.portfolio.entity.PortfolioJpaEntity;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Entity
@Table(name = "portfolio_image")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioImageJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private PortfolioJpaEntity portfolio;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

}