package com.auth.application.feign.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginAndPassword {

    @NonNull
    private String login;

    @NonNull
    private String password;
}
