package com.auth.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.auth.RoleMocks;
import com.auth.application.exception.InvalidAttributeValueException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

class UserTest {

    @Test
    void shouldNotCreateUserWithEmptyRoles() {
        assertThrows(InvalidAttributeValueException.class,
                () -> User.create("login", "pass", "em@em", new HashSet<>()));
    }

    @Test
    void shouldCreateUser() throws JsonMappingException, JsonProcessingException, InvalidAttributeValueException {
        assertThat(User.create("login", "pass", "em@em", Set.of(RoleMocks.userRoleMock()))).isNotNull();
    }

}
