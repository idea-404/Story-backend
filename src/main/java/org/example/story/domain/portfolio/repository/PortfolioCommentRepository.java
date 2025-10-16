package org.example.story.domain.portfolio.repository;

import org.example.story.domain.portfolio.entity.PortfolioCommentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioCommentRepository extends JpaRepository<PortfolioCommentJpaEntity, Long> {

    List<PortfolioCommentJpaEntity> findByUserId(Long portfolioId);
}
