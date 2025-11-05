package org.example.story.global.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfig {

    @Value("openai.api-key")
    private String apiKey;

    @Value("openai.timeout-seconds")
    private long timeoutSeconds;

    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(apiKey, java.time.Duration.ofSeconds(timeoutSeconds));
    }
}
