package org.example.story.domain.blog.repository;

import org.example.story.domain.blog.entity.BlogJpaEntity;
import org.example.story.domain.blog.entity.BlogLikeJpaEntity;
import org.example.story.domain.user.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogLikeRepository extends JpaRepository<BlogLikeJpaEntity, Long> {

    Optional<BlogLikeJpaEntity> findByBlogAndUser(BlogJpaEntity blog, UserJpaEntity user);

    void deleteByBlogIdAndUserId(Long blogId, Long userId);
}
