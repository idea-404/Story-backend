package org.example.story.domain.blog.repository;

import org.example.story.domain.blog.entity.BlogCommentJpaEntity;
import org.example.story.domain.blog.entity.BlogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogCommentRepository extends JpaRepository<BlogCommentJpaEntity, Long> {

    Optional<BlogCommentJpaEntity> findByBlogIdAndId(Long blogId, Long commentId);

    List<BlogCommentJpaEntity> findByBlogOrderByIdDesc(BlogJpaEntity blog);
}
