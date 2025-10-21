package org.example.story.global.error.handler;

import org.example.story.global.error.data.response.ErrorResponse;
import org.example.story.global.error.exception.ExpectedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ExpectedException.class)
    public ResponseEntity<ErrorResponse> handleExpectedException(ExpectedException e) {
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(new ErrorResponse(
                        e.getHttpStatus().value(), e.getMessage()
                ));
    }
}
