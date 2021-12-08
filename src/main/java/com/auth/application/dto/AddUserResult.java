package com.auth.application.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AddUserResult {

    private Map<String, String> errors = new HashMap<>();

    public Map<String, String> getErrors() {
        return errors;
    }

    public void addError(Property property, ErrorMessage errorMessage) {
        errors.put(property.getValue(), errorMessage.getValue());
    }

    public boolean allValid() {
        return errors.isEmpty();
    }

    @AllArgsConstructor
    @Getter
    public static enum Property {
        
        LOGIN("login"),
        EMAIL("email");

        private String value;
    }

    @AllArgsConstructor
    @Getter
    public static enum ErrorMessage {

        ALREADY_TAKEN("Already taken");

        private String value;
    }
}
