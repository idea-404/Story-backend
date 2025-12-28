package org.example.story.global.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient(
            @Value("${spring.minio.endpoint}") String endpoint,
            @Value("${spring.minio.access-key}") String accessKey,
            @Value("${spring.minio.secret-key}") String secretKey
    ) {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
