package com.example.userregistration.exception;

import org.springframework.http.HttpStatus;

public class ServiceException extends BaseException {

    public ServiceException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
        this.initCause(cause);
    }
}
