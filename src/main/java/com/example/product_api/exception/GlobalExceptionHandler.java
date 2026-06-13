package com.example.product_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler that converts unhandled RuntimeExceptions thrown
 * by the service layer into proper HTTP error responses instead of letting
 * Spring propagate them as NestedServletException.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles business-rule violations:
     *  - Product not found
     *  - Insufficient stock
     *  - Order not found
     *  - Order cannot be cancelled (not PENDING)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }
}
