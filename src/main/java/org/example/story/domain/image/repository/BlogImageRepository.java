package org.example.story.domain.image.repository;

import org.example.story.domain.image.entity.BlogImageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogImageRepository extends JpaRepository<BlogImageJpaEntity, Long> {
    List<BlogImageJpaEntity> findByBlog_Id(Long blogId);

}
