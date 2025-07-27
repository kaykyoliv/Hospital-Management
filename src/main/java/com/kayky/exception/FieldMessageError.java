package com.kayky.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldMessageError {
    private final String fieldName, message;
}
