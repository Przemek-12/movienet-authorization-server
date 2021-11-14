package com.auth.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum RoleName {

    ROLE_USER("ROLE_USER"), ROLE_ADMIN("ROLE_ADMIN");
    
    private String value;
    
}
