package com.auth.application.feign.dto;

import com.auth.application.dto.LoginCredentials;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class AddUserRequest {

    @NonNull
    private LoginCredentials loginCredentials;

    @NonNull
    private String email;

}
