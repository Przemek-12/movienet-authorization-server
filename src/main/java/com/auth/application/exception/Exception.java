package com.auth.application.exception;

import org.springframework.http.HttpStatus;

public interface Exception {

    HttpStatus getStatus();

    String getMessage();
}
