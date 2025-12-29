package org.example.story.domain.ola.record.response;

public record OlaResponse(
        Long portfolioId,
        String question,
        String answer
) {
}
