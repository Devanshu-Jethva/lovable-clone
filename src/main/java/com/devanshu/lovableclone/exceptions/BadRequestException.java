package com.devanshu.lovableclone.exceptions;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class BadRequestException extends BaseException {

    @Serial
    private static final long serialVersionUID = 202335407865995227L;

    /**
     * Constructor with message
     */
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "BAD_REQUEST");
    }

}
