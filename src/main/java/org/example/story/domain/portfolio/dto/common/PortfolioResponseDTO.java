package org.example.story.domain.portfolio.dto.common;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioResponseDTO {
    // 공통으로 사용되는 응답 dto
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Long like;
    private Long view;
    private Long comment;
    private Boolean zerodog;
    private Instant createdAt;
}
