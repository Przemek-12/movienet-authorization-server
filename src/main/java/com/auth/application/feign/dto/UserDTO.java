package com.auth.application.feign.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@NoArgsConstructor
public class UserDTO {

    @NonNull
    private Long id;

    @NonNull
    private String login;

    @NonNull
    private String email;

    @Builder
    public static UserDTO create(Long id, String login, String email) {
        return new UserDTO(id, login, email);
    }
}
