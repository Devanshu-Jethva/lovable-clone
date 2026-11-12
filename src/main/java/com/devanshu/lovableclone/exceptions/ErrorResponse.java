package com.devanshu.lovableclone.exceptions;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

	private String errorCode;
	private String message;
	private Instant timestamp;

	private List<FieldError> fieldErrors;

	public static ErrorResponse of(final String errorCode, final String message) {
		return ErrorResponse.builder().errorCode(errorCode).message(message).timestamp(Instant.now()).build();
	}

	public static ErrorResponse of(final String errorCode, final String message, final List<FieldError> fieldErrors) {
		return ErrorResponse.builder().errorCode(errorCode).message(message).timestamp(Instant.now())
				.fieldErrors(fieldErrors).build();
	}
}