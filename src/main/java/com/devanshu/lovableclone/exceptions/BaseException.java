package com.devanshu.lovableclone.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

@Getter
public abstract class BaseException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 5940050654928085147L;

    /**
     * Get HTTP status for this exception
     * Each custom exception will define its own status
     */
    private final HttpStatus status;
    /**
     * Get error code for client-side error handling
     * Each custom exception will define its own error code
     */
    private final String errorCode;

    protected BaseException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

}
