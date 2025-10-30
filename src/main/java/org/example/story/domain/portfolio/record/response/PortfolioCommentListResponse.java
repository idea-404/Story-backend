package org.example.story.domain.portfolio.record.response;

import java.util.List;

public record PortfolioCommentListResponse(
        Long portfolioId,
        List<PortfolioCommentResponse> comments
) { }
