package com.kayky.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        var status = HttpStatus.NOT_FOUND;

        return ResponseEntity.status(status).body(buildError(status, e.getMessage(), request));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyExists(EmailAlreadyExistsException e, HttpServletRequest request) {
        var status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(buildError(status, e.getMessage(), request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request){
        var status = HttpStatus.UNPROCESSABLE_ENTITY;

        ValidationError error = new ValidationError(
                Instant.now(),
                status.value(),
                e.getMessage(),
                request.getRequestURI()
        );

        for(FieldError f : e.getBindingResult().getFieldErrors()){
            error.addError(f.getField(), f.getDefaultMessage());
        }

        return ResponseEntity.status(status).body(error);
    }

    private ApiError buildError(HttpStatus status, String message, HttpServletRequest request) {
        return new ApiError(Instant.now(), status.value(), message, request.getRequestURI());
    }

}
