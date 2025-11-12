package org.example.story.global.config;

import jakarta.persistence.EntityManager;
import org.example.story.domain.blog.entity.BlogJpaEntity;
import org.example.story.domain.main.repository.GenericCursorRepository;
import org.example.story.domain.main.repository.GenericCursorRepositoryImpl;
import org.example.story.domain.portfolio.entity.PortfolioJpaEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {
    @Bean
    public GenericCursorRepository<PortfolioJpaEntity> portfolioCursorRepo(EntityManager em) {
        return new GenericCursorRepositoryImpl<>(PortfolioJpaEntity.class);
    }
    @Bean
    public GenericCursorRepository<BlogJpaEntity> blogCursorRepo(EntityManager em) {
        return new GenericCursorRepositoryImpl<>(BlogJpaEntity.class);
    }

}
