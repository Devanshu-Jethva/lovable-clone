package com.devanshu.lovableclone.exceptions;

import org.springframework.http.HttpStatus;

import java.io.Serial;

public class PermissionException extends BaseException {

    @Serial
    private static final long serialVersionUID = 3683405498524614362L;

    public PermissionException(String message) {
        super(message, HttpStatus.FORBIDDEN, "FORBIDDEN");
    }
}
