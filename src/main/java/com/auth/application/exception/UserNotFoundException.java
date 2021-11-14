package com.auth.application.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends IOException implements Exception {

    private static final long serialVersionUID = 1L;

    public UserNotFoundException() {
        super();
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getMessage() {
        return "User not found.";
    }

}
