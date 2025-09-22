package com.example.playpal.auth.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends RuntimeException {
    public static final HttpStatus STATUS = HttpStatus.CONFLICT;

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
