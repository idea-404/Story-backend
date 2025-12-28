package org.example.story.domain.image.repository;

import org.example.story.domain.image.entity.PortfolioImageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioImageRepository extends JpaRepository<PortfolioImageJpaEntity, Long> {
    List<PortfolioImageJpaEntity> findByPortfolio_Id(Long portfolioId);

}
