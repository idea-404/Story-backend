package org.example.story.domain.main.record;


import java.util.List;

public record PortfolioListResponse(
        List<PortfolioViewResponse> portfolio
) {
}
