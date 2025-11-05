package org.example.story.global.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfig {

    @Value("${AI_KEY}")
    private String apiKey;

    @Bean
    public OpenAiService openAiService() {
        // 두 번째 인자는 타임아웃 (초 단위) — 예: 60초
        return new OpenAiService(apiKey);
    }
}
