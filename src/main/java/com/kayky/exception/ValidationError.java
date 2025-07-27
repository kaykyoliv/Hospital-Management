package com.kayky.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ValidationError extends ApiError{

    @JsonProperty("fieldErrors")
    List<FieldMessageError> errors = new ArrayList<>();

    public ValidationError(Instant timestamp, int status, String error, String path) {
        super(timestamp, status, error, path);
    }

    public void addError(String fieldName, String message){
        errors.removeIf(error -> error.getFieldName().equals(fieldName));
        errors.add(new FieldMessageError(fieldName, message));
    }
}
