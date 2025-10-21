package org.example.story.global.error.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExpectedException extends RuntimeException {
    HttpStatus httpStatus;
    public ExpectedException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
