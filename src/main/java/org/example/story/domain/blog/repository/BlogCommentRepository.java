package org.example.story.domain.blog.repository;

import org.example.story.domain.blog.entity.BlogCommentJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogCommentRepository extends JpaRepository<BlogCommentJpaEntity, Long> {


    @Query("SELECT c FROM BlogCommentJpaEntity c " +
            "WHERE c.blog.id = :blogId " +
            "AND (:lastId IS NULL OR c.id < :lastId) " +
            "ORDER BY c.id DESC")
    List<BlogCommentJpaEntity> findCommentsAfterCursor(
            @Param("blogId") Long blogId,
            @Param("lastId") Long lastId,
            Pageable pageable
    );

    Optional<BlogCommentJpaEntity> findByBlogIdAndId(Long blogId, Long commentId);
}
