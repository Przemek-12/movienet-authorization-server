package com.auth.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PermissionName {

    WRITE("write"),
    READ("read");

    @Getter
    private String value;

}
