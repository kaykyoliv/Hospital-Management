package com.kayky.core.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class ApiError {

    private Instant timestamp;
    private int status;
    private String error;
    private String path;
}
