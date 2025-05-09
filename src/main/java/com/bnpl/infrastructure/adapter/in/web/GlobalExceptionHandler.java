package com.bnpl.infrastructure.adapter.in.web;

import com.bnpl.domain.model.error.BnplException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BnplException.class)
    public ResponseEntity<ErrorResponse> handleBnplException(BnplException ex, HttpServletRequest request) {
        log.error("BNPL exception: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                ex.getCode(),
                ex.getError(),
                System.currentTimeMillis() / 1000,
                ex.getMessage(),
                ex.getPath() != null ? ex.getPath() : request.getRequestURI()
        );
        
        HttpStatus status;
        switch (ex.getError()) {
            case "CUSTOMER_NOT_FOUND", "LOAN_NOT_FOUND":
                status = HttpStatus.NOT_FOUND;
                break;
            case "INVALID_CUSTOMER_REQUEST", "INVALID_LOAN_REQUEST", "INSUFFICIENT_CREDIT_LINE":
                status = HttpStatus.BAD_REQUEST;
                break;
            case "UNAUTHORIZED":
                status = HttpStatus.UNAUTHORIZED;
                break;
            default:
                status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.error("Validation exception: {}", errorMessage, ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "APZ000004",
                "INVALID_REQUEST",
                System.currentTimeMillis() / 1000,
                errorMessage,
                request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Internal server error: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
                "APZ000001",
                "INTERNAL_SERVER_ERROR",
                System.currentTimeMillis() / 1000,
                "An unexpected error occurred",
                request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}