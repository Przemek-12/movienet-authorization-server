package com.auth.application.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor
public class LoginCredentials {

    // base64 encoded
    @NonNull
    private String loginCredentials;

}
