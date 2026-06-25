package com.devanshu.lovableclone.exceptions;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FieldError {
    private String field;
    private String message;
    private Object rejectedValue;
}
