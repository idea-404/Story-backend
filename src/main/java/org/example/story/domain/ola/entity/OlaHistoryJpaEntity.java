package org.example.story.domain.ola.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.story.domain.portfolio.entity.PortfolioJpaEntity;

@Getter
@Entity
@Table(name = "ola_history")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OlaHistoryJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qa_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private PortfolioJpaEntity portfolio;

    @Lob
    @Column(name = "question", nullable = false)
    private String question;

    @Lob
    @Column(name = "answer", nullable = false)
    private String answer;

}