package org.example.story.domain.ola.record.response;

import java.util.List;

public record OlaListResponse(
        Long portfolioId,
        List<OlaResponse> records
) {
}
