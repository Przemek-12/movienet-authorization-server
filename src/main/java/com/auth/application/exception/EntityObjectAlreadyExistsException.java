package com.auth.application.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;

public class EntityObjectAlreadyExistsException extends IOException implements Exception {

    private static final long serialVersionUID = 1L;

    private final String entityName;

    public EntityObjectAlreadyExistsException(String entityName) {
        this.entityName = entityName;
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.CONFLICT;
    }

    @Override
    public String getMessage() {
        return new StringBuilder().append(entityName).append(" already exists.").toString();
    }

}
