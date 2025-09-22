package com.example.playpal.common.exception;

import org.springframework.http.HttpStatus;

public class UnprocessableContentException extends RuntimeException {
    public static final HttpStatus STATUS = HttpStatus.UNPROCESSABLE_ENTITY;

    public UnprocessableContentException(String message) {
        super(message);
    }
}
