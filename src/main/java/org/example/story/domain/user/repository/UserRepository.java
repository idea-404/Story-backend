package org.example.story.domain.user.repository;


import org.example.story.domain.user.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserJpaEntity, Long> {
}
