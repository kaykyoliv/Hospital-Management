package com.kayky.core.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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

    @ExceptionHandler(OperationMismatchException.class)
    public ResponseEntity<ApiError> handleOperationMismatch(OperationMismatchException e, HttpServletRequest request) {
        var status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(buildError(status, e.getMessage(), request));
    }

    @ExceptionHandler(ReportAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleReportAlreadyExists(ReportAlreadyExistsException e, HttpServletRequest request) {
        var status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(buildError(status, e.getMessage(), request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request){
        var status = HttpStatus.UNPROCESSABLE_ENTITY;

        ValidationError error = new ValidationError(
                Instant.now(),
                status.value(),
                "Validation failed",
                request.getRequestURI()
        );

        List<FieldError> fieldErrorList = e.getBindingResult().getFieldErrors();

        fieldErrorList.stream()
                .sorted(Comparator.comparing(FieldError::getField))
                .forEach(f -> error.addError(f.getField(), f.getDefaultMessage()));


        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException e, HttpServletRequest request){
        var status = HttpStatus.BAD_REQUEST;

        String errorMessage = "Malformed Json request";

        Throwable cause = e.getCause();

        if(cause instanceof InvalidFormatException invalidFormatException){
            if(invalidFormatException.getTargetType().isEnum()){
                String acceptedValues = String.join(", ",
                        Arrays.stream(invalidFormatException.getTargetType().getEnumConstants())
                                .map(Object::toString)
                                .toList());
                errorMessage = String.format("Invalid value '%s' for enum %s. Accepted values are: [%s]",
                        invalidFormatException.getValue(),
                        invalidFormatException.getTargetType().getSimpleName(),
                        acceptedValues
                );
            }
        }
        return ResponseEntity.status(status).body(buildError(status, errorMessage, request));
    }

    private ApiError buildError(HttpStatus status, String message, HttpServletRequest request) {
        return new ApiError(Instant.now(), status.value(), message, request.getRequestURI());
    }

}
