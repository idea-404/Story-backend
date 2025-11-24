package org.example.story.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "kakao")
public class KakaoOAuthConfig {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
}
