package com.auth.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AddUserResult {

    private boolean validLogin = true;
    private boolean validEmail = true;

    public void invalidLogin() {
        this.validLogin = false;
    }

    public void invalidEmail() {
        this.validEmail = false;
    }

    public boolean allValid() {
        return (validLogin && validEmail);
    }
}
