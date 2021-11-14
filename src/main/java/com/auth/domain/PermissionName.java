package com.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PermissionName {

    WRITE("write");

    @Getter
    private String value;

}
