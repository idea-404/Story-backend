package org.example.story.domain.ola.repository;

import org.example.story.domain.ola.entity.OlaHistoryJpaEntity;
import org.example.story.domain.portfolio.entity.PortfolioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OlaRepository extends JpaRepository<OlaHistoryJpaEntity, Long> {
    List<OlaHistoryJpaEntity> findByPortfolioOrderByIdDesc(PortfolioJpaEntity portfolio);

}
