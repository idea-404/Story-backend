package org.example.story.domain.portfolio.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioCommentRequestDTO {
    // 이름이 곧 역할인 dto
    private String content;
}
