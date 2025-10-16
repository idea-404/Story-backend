package org.example.story.domain.portfolio.dto.response;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioCommentResponseDTO {
    // 이름이 곧 역할인 dto
    private Long id;
    private Long portfolioId;
    private Long userId;
    private String content;
    private Instant createdAt;
}
