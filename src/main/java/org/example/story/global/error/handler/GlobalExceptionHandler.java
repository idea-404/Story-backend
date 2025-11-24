package org.example.story.global.error.handler;

import org.apache.coyote.Response;
import org.example.story.global.error.data.response.ErrorResponse;
import org.example.story.global.error.exception.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        throw new ExpectedException(HttpStatus.BAD_REQUEST, "Null이 올 수 없는 속성에 Null이 들어왔습니다.");
    }
}
