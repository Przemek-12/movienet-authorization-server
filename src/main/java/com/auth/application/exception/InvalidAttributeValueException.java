package com.auth.application.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;

public class InvalidAttributeValueException extends IOException implements Exception {

    private static final long serialVersionUID = 1L;

    public InvalidAttributeValueException() {
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public String getMessage() {
        return "Invalid argument value.";
    }
}
