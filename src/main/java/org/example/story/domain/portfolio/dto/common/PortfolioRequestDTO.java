package org.example.story.domain.portfolio.dto.common;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioRequestDTO {
    // 공통으로 사용되는 요청 dto
    private String title;
    private String content;
}
