package com.devanshu.lovableclone.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice(basePackages = "com.devanshu")
public class GlobalExceptionHandler {

    /**
     * ONE handler for all BaseException and its subclasses
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex, HttpServletRequest request) {
        log.error("BaseException error occurred: {}", request.getRequestURL(), ex);
        return ResponseEntity.status(ex.getStatus())
                             .body(ErrorResponse.of(ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("Validation error occurred: {}", request.getRequestURL(), ex);

        List<FieldError> fieldErrors = ex.getBindingResult()
                                         .getFieldErrors()
                                         .stream()
                                         .map(error -> FieldError.builder()
                                                                 .field(error.getField())
                                                                 .message(error.getDefaultMessage())
                                                                 .rejectedValue(error.getRejectedValue())
                                                                 .build())
                                         .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(ErrorResponse.of("VALIDATION_ERROR", "Request validation failed", fieldErrors));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        log.error("Authentication error occurred: {}", request.getRequestURL(), ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(ErrorResponse.of("UNAUTHORIZED", "Please login first"));
    }

    @ExceptionHandler({AccessDeniedException.class, SecurityException.class})
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(Exception ex, HttpServletRequest request) {
        log.error("Access denied error occurred: {}", request.getRequestURL(), ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                             .body(ErrorResponse.of("FORBIDDEN", "You don't have permission to perform this action"));
    }

    /**
     * Catch-all for unexpected exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Throwable ex, HttpServletRequest request) {
        log.error("Unexpected error occurred: {}", request.getRequestURL(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(ErrorResponse.of("INTERNAL_SERVER_ERROR",
                                     "An unexpected error occurred. Please try again later."));
    }
}
