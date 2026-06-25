package com.devanshu.lovableclone.exceptions;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class NotFoundException extends BaseException {

    @Serial
    private static final long serialVersionUID = 4766427040277590349L;

    /**
     * Constructor with message
     */
    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "NOT_FOUND");
    }
}
