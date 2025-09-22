package com.example.playpal.common.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends RuntimeException {
    public static final HttpStatus STATUS = HttpStatus.UNAUTHORIZED;

    public UnauthorizedException(String message) {
        super(message);
    }
}
