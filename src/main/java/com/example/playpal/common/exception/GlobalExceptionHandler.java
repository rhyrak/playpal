package com.example.playpal.common.exception;

import com.example.playpal.auth.exception.UserAlreadyExistsException;
import com.example.playpal.common.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
        return response(e.getMessage(), ResourceNotFoundException.STATUS);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException e) {
        return response(e.getMessage(), UnauthorizedException.STATUS);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException e) {
        return response(e.getMessage(), ForbiddenException.STATUS);
    }

    @ExceptionHandler(UnprocessableContentException.class)
    public ResponseEntity<ErrorResponse> handleUnprocessableContentException(UnprocessableContentException e) {
        return response(e.getMessage(), UnprocessableContentException.STATUS);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        return response(e.getMessage(), UserAlreadyExistsException.STATUS);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();

        var res = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message("validation error")
                .status(e.getStatusCode().value())
                .errors(errors)
                .build();

        return ResponseEntity.status(e.getStatusCode()).body(res);
    }

    private ResponseEntity<ErrorResponse> response(String message, HttpStatus status) {
        return new ResponseEntity<>(ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(message)
                .status(status.value())
                .build(), status);
    }
}
