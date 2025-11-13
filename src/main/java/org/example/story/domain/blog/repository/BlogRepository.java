package org.example.story.domain.blog.repository;

import org.example.story.domain.blog.entity.BlogJpaEntity;
import org.example.story.domain.main.repository.GenericCursorRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<BlogJpaEntity, Long>,
        GenericCursorRepository<BlogJpaEntity> {
    Optional<BlogJpaEntity> findByIdAndUserId(Long id, Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE BlogJpaEntity b SET b.comment = b.comment + 1 WHERE b.id = :blogId")
    void incrementComment(Long blogId);

    @Modifying
    @Transactional
    @Query("UPDATE BlogJpaEntity b SET b.comment = b.comment - 1 WHERE b.id = :blogId")
    void decrementComment(Long blogId);
}
