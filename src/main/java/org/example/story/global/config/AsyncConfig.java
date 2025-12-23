package org.example.story.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfig {

    @Bean(name = "openAiExecutor")
    public Executor openAiExecutor() {
        return Executors.newFixedThreadPool(5);
    }
}
