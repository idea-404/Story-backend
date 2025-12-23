package org.example.story.domain.main.record;

import java.util.List;

public record SearchListResponse(
        List<PortfolioViewsResponse> portfolio,
        List<BlogViewsResponse> blog
) {
}
