package com.social.mc_account.controller;

import com.social.mc_account.exception.AppError;
import com.social.mc_account.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<AppError> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage(), ex);
        AppError appError = new AppError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(appError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AppError> handleGenericException(Exception ex) {
        log.error("An error occurred: {}", ex.getMessage(), ex);
        AppError appError = new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred");
        return new ResponseEntity<>(appError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
