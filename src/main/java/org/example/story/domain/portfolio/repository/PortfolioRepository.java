package org.example.story.domain.portfolio.repository;


import org.example.story.domain.portfolio.entity.PortfolioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioJpaEntity, Long> {
    Optional<PortfolioJpaEntity> findByIdAndUser_Id(Long id, Long userId);

    @Query("""
            select p from PortfolioJpaEntity p
            join fetch p.user
            where p.user.id = :userId
            """)
    List<PortfolioJpaEntity> findByUserId(Long userId);
}
