package org.example.story.global.error.data.response;

public record ErrorResponse(
        int httpStatus,
        String message
) {
}