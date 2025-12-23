package org.example.story.global.config;

import org.example.story.domain.blog.entity.BlogJpaEntity;
import org.example.story.domain.main.repository.GenericCursorRepository;
import org.example.story.domain.main.repository.GenericCursorRepositoryCustom;
import org.example.story.domain.portfolio.entity.PortfolioJpaEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {
    @Bean
    public GenericCursorRepository<PortfolioJpaEntity> portfolioCursorRepo(){
        return new GenericCursorRepositoryCustom<>(PortfolioJpaEntity.class);
    }
    @Bean
    public GenericCursorRepository<BlogJpaEntity> blogCursorRepo(){
        return new GenericCursorRepositoryCustom<>(BlogJpaEntity.class);
    }

}
