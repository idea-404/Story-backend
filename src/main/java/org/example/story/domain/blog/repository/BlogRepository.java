package org.example.story.domain.blog.repository;

import org.example.story.domain.blog.entity.BlogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<BlogJpaEntity, Long> {
    Optional<BlogJpaEntity> findByIdAndUser_Id(Long id, Long userId);

    @Query("""
            select b from BlogJpaEntity b
            join fetch b.user
            where b.user.id = :userId
            """)
    List<BlogJpaEntity> findByUserId(Long userId);
}
