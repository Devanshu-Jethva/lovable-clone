package com.devanshu.lovableclone.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ErrorResponse {

    private String errorCode;
    private String message;
    private Instant timestamp;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FieldError> fieldErrors;

    public static ErrorResponse of(String errorCode, String message) {
        return ErrorResponse.builder()
                            .errorCode(errorCode)
                            .message(message)
                            .timestamp(Instant.now())
                            .build();
    }

    public static ErrorResponse of(String errorCode, String message, List<FieldError> fieldErrors) {
        return ErrorResponse.builder()
                            .errorCode(errorCode)
                            .message(message)
                            .timestamp(Instant.now())
                            .fieldErrors(fieldErrors)
                            .build();
    }
}