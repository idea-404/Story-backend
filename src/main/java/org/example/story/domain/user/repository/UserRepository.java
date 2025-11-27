package org.example.story.domain.user.repository;


import org.example.story.domain.user.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserJpaEntity, Long> {
    boolean existsByEmail(String email);

    Optional<UserJpaEntity> findByEmail(String email);
}
