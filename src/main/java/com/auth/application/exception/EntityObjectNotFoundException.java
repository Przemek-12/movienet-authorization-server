package com.auth.application.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;

public class EntityObjectNotFoundException extends IOException implements Exception {

    private static final long serialVersionUID = 1L;

    private final String entityName;

    public EntityObjectNotFoundException(String entityName) {
        this.entityName = entityName;
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getMessage() {
        return new StringBuilder().append(entityName).append(" not found.").toString();
    }
}
