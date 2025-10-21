package org.example.story.domain.portfolio.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.example.story.domain.portfolio.entity.PortfolioCommentJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioCommentRepository extends JpaRepository<PortfolioCommentJpaEntity, Long> {

    List<PortfolioCommentJpaEntity> findByUserId(Long portfolioId);

    @Query("SELECT c FROM PortfolioCommentJpaEntity c " +
            "WHERE c.portfolio.id = :portfolioId " +
            "AND (:lastId IS NULL OR c.id < :lastId) " +
            "ORDER BY c.id DESC")
    List<PortfolioCommentJpaEntity> findCommentsAfterCursor(
            @Param("portfolioId") Long portfolioId,
            @Param("lastId") Long lastId,
            Pageable pageable
    );

    Optional<PortfolioCommentJpaEntity> findByPortfolioIdAndId(Long portfolioId, Long commentId);
}
