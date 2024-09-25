package com.noom.interview.fullstack.sleep.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that handles user and checked exceptions thrown by the application and wrap them into a
 * standard error response.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessages);
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentTypeMismatchException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, Collections.singletonList(ex.getMostSpecificCause().getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(ResourceNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, Collections.singletonList(ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleException(BadRequestException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, Collections.singletonList(ex.getMessage()));
    }

    // Generic Exception handler to catch all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        // In a real case scenario a better logging tool should be used (e.g. SLF4J, Apache Commons Logging)
        System.err.println(ex.getMessage());
        System.err.println(ex.getStackTrace());
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, Collections.singletonList("An unexpected error occurred."));
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, List<String> errorMessages) {
        return new ResponseEntity<>(new ErrorResponse(status, Instant.now(), errorMessages), new HttpHeaders(), status);
    }

    @Getter
    @AllArgsConstructor
    static class ErrorResponse {
        private HttpStatus status;

        private Instant timestamp;

        private List<String> errors;
    }
}
