package org.example.story.domain.portfolio.dto.response;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioLikeResponseDTO {
    // 이름이 곧 역할인 dto
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Long like;
    private Long view;
    private Long comment;
    private Boolean zerodog;
    private Instant createdAt;
    private Boolean liked;
}
