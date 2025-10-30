package org.example.story.domain.portfolio.repository;

import org.example.story.domain.portfolio.entity.PortfolioJpaEntity;
import org.example.story.domain.portfolio.entity.PortfolioLikeJpaEntity;
import org.example.story.domain.user.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortfolioLikeRepository extends JpaRepository<PortfolioLikeJpaEntity, Long> {

    Optional<PortfolioLikeJpaEntity> findByPortfolioAndUser(PortfolioJpaEntity portfolio, UserJpaEntity user);

    void deleteByPortfolioIdAndUserId(Long portfolioId, Long userId);
}
