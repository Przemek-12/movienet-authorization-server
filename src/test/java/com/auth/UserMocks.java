package com.auth;

import java.util.Base64;
import java.util.Set;

import com.auth.application.dto.LoginCredentials;
import com.auth.application.exception.InvalidAttributeValueException;
import com.auth.application.feign.dto.AddUserRequest;
import com.auth.domain.entity.Role;
import com.auth.domain.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMocks {

    public static User userMock() throws InvalidAttributeValueException, JsonMappingException, JsonProcessingException {
        return User.create("login", "pass", "em@em", Set.of(RoleMocks.userRoleMock()));
    }

    public static User userMock(Set<Role> roles) throws InvalidAttributeValueException {
        return User.create("login", "pass", "em@em", roles);
    }

    public static AddUserRequest addUserRequestMock() {
        return new AddUserRequest(
                new LoginCredentials(
                        Base64.getEncoder().encodeToString("john:wick".getBytes())),
                "em@em");
    }

}
