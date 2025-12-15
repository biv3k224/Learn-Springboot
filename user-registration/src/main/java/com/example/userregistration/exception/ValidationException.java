package com.example.userregistration.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class ValidationException extends BaseException {

    private final Map<String, String> validationErrors;
    public ValidationException(String message, Map<String, String> validationErrors) {
        super(message, HttpStatus.BAD_REQUEST);
        this.validationErrors = validationErrors;
    }

    public ValidationException(Map<String, String> validationErrors) {
        super("Validation failed", HttpStatus.BAD_REQUEST);
        this.validationErrors = validationErrors;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }
}
