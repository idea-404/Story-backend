package org.example.story.domain.portfolio.repository;

import org.example.story.domain.portfolio.entity.PortfolioJpaEntity;
import org.example.story.domain.portfolio.entity.PortfolioCommentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioCommentRepository extends JpaRepository<PortfolioCommentJpaEntity, Long> {

    Optional<PortfolioCommentJpaEntity> findByPortfolioIdAndId(Long portfolioId, Long commentId);

    List<PortfolioCommentJpaEntity> findByPortfolioOrderByIdDesc(PortfolioJpaEntity portfolio);
}
