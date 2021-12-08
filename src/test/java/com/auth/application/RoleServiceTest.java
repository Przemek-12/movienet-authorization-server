package com.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.auth.RoleMocks;
import com.auth.application.exception.EntityObjectNotFoundException;
import com.auth.domain.entity.RoleName;
import com.auth.domain.repository.RoleRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @InjectMocks
    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;

    private void whenRoleRepositoryFindByNameTheReturnEmptyOptional() {
        when(roleRepository.findByName(Mockito.any(RoleName.class)))
                .thenReturn(Optional.empty());
    }

    private void whenRoleRepositoryFindByNameTheReturnUserRole() throws JsonMappingException, JsonProcessingException {
        when(roleRepository.findByName(Mockito.any(RoleName.class)))
                .thenReturn(Optional.of(RoleMocks.userRoleMock()));
    }

    @Test
    void shouldThroExceptionIfRoleNotFound() {
        whenRoleRepositoryFindByNameTheReturnEmptyOptional();
        Throwable exception = assertThrows(EntityObjectNotFoundException.class, () -> roleService.getUserRole());
        assertThat(exception.getMessage()).isEqualTo("Role not found.");
    }

    @Test
    void shouldGetUserRole() throws EntityObjectNotFoundException, JsonMappingException, JsonProcessingException {
        whenRoleRepositoryFindByNameTheReturnUserRole();
        assertThat(roleService.getUserRole().getName()).isEqualTo(RoleName.ROLE_USER);
    }

}
