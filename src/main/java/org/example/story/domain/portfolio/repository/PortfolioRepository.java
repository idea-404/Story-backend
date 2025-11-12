package org.example.story.domain.portfolio.repository;


import org.example.story.domain.main.repository.GenericCursorRepository;
import org.example.story.domain.portfolio.entity.PortfolioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioJpaEntity, Long>,
        GenericCursorRepository<PortfolioJpaEntity> {
    Optional<PortfolioJpaEntity> findByIdAndUserId(Long id, Long userId);
}
