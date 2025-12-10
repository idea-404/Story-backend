package org.example.story.domain.image.record.request;

import java.util.List;

public record ImageRequest(
        List<String> fileKeys
) {
}
