package com.example.playpal.common.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends RuntimeException {
    public static final HttpStatus STATUS = HttpStatus.FORBIDDEN;

    public ForbiddenException(String message) {
        super(message);
    }
}
