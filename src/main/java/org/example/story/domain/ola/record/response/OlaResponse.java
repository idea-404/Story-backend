package org.example.story.domain.ola.record.response;

public record OlaResponse(
        Long id,
        Long portfolioId,
        String question,
        String answer
) {
}
