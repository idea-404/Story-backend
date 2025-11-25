package org.example.story.global.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.story.global.error.exception.ExpectedException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.View;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Configuration
public class AppConfig {
    private final ObjectMapper objectMapper;
    private final View error;

    public AppConfig(ObjectMapper objectMapper, View error) {
        this.objectMapper = objectMapper;
        this.error = error;
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return response.getStatusCode().isError();
            }

            @Override
            public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
                String responseBody = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
                String errorMessage = responseBody;
                try {
                    JsonNode jsonNode = objectMapper.readTree(responseBody);
                    String error = jsonNode.get("error").asText("");
                    String errorDescription = jsonNode.path("error_description").asText("");
                    if(!errorDescription.isEmpty()) {
                        errorMessage = String.format("%s (%s)", errorDescription, error);
                    }
                } catch (Exception e) {

                }
                HttpStatus status = (HttpStatus) response.getStatusCode();
                HttpStatus finalStatus = (status == HttpStatus.BAD_REQUEST || status == HttpStatus.UNAUTHORIZED)
                        ? status : HttpStatus.INTERNAL_SERVER_ERROR;
                throw new ExpectedException(
                        finalStatus, "OAuth 요청에 실패: " + errorMessage);
            }
        });
        return restTemplate;
    }
}
