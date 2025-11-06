package org.example.story.domain.blog.repository;

import org.example.story.domain.blog.entity.BlogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<BlogJpaEntity, Long> {

    // 유저 아이디로 작성글 조회
    List<BlogJpaEntity> findByUserId(Long userId);

    // 제목으로 검색
    List<BlogJpaEntity> findByTitleContaining(String keyword);

    // 조회수가 많은 순으로 정렬 조회
    List<BlogJpaEntity> findAllByOrderByViewDesc();

    Optional<BlogJpaEntity> findByIdAndUserId(Long id, Long userId);

}
