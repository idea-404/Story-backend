package org.example.story.domain.blog.repository;

import org.example.story.domain.blog.entity.BlogJpaEntity;
import org.example.story.domain.main.repository.GenericCursorRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<BlogJpaEntity, Long>,
        GenericCursorRepository<BlogJpaEntity> {
    Optional<BlogJpaEntity> findByIdAndUserId(Long id, Long userId);
}
