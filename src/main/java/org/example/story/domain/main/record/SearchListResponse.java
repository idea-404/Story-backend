package org.example.story.domain.main.record;

import java.util.List;

public record SearchListResponse(
        List<ListResponse> portfolio,
        List<ListResponse> blog
) {
}
