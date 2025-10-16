package org.example.story.domain.portfolio.repository;

import org.example.story.domain.portfolio.entity.PortfolioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<PortfolioJpaEntity, Long> {

    // 유저 아이디로 작성글 조회
    List<PortfolioJpaEntity> findByUserId(Long userId);

    // 제목으로 검색
    List<PortfolioJpaEntity> findByTitleContaining(String keyword);

    // 공개여부가 true인 포폴만 조회
    List<PortfolioJpaEntity> findByZerodogTrue();

    // 조회수가 많은 순으로 정렬 조회
    List<PortfolioJpaEntity> findAllByOrderByViewDesc();

}
