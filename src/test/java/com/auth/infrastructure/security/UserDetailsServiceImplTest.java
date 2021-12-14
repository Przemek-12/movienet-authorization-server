package com.auth.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.auth.RoleMocks;
import com.auth.UserMocks;
import com.auth.application.UserService;
import com.auth.application.exception.EntityObjectNotFoundException;
import com.auth.application.exception.InvalidAttributeValueException;
import com.auth.domain.entity.PermissionName;
import com.auth.domain.entity.Role;
import com.auth.domain.entity.RoleName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Mock
    private UserService userService;

    private void whenUserServiceGetEntityByLoginThenThrowException() throws EntityObjectNotFoundException {
        when(userService.getEntityByLogin(Mockito.anyString())).thenThrow(EntityObjectNotFoundException.class);
    }

    private void whenUserServiceGetEntityByLoginThenReturnUserWithRoles(Set<Role> roles)
            throws EntityObjectNotFoundException, InvalidAttributeValueException {
        when(userService.getEntityByLogin(Mockito.anyString())).thenReturn(UserMocks.userMock(roles));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() throws EntityObjectNotFoundException {
        whenUserServiceGetEntityByLoginThenThrowException();

        assertThrows(UsernameNotFoundException.class, () -> userDetailsServiceImpl.loadUserByUsername("login"));
    }

    @Test
    void shouldAddPermissionsAndRolesAsAuthorities()
            throws EntityObjectNotFoundException, JsonMappingException, JsonProcessingException, JSONException,
            InvalidAttributeValueException {
        Set<Role> roles = Set.of(
                RoleMocks.roleMock(
                        RoleName.ROLE_USER,
                        Set.of(
                                RoleMocks.permissionMock(PermissionName.READ))),
                RoleMocks.roleMock(
                        RoleName.ROLE_ADMIN,
                        Set.of(
                                RoleMocks.permissionMock(PermissionName.WRITE))));

        whenUserServiceGetEntityByLoginThenReturnUserWithRoles(roles);

        UserDetails details = userDetailsServiceImpl.loadUserByUsername("login");

        assertThat(details.getAuthorities()).hasSize(4);
        assertThat(details.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(RoleName.ROLE_USER.getValue()))).isTrue();
        assertThat(details.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(RoleName.ROLE_ADMIN.getValue()))).isTrue();
        assertThat(details.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority()
                        .equals(RoleName.ROLE_USER.getValue() + "_" + PermissionName.READ.getValue()))).isTrue();
        assertThat(details.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority()
                        .equals(RoleName.ROLE_ADMIN.getValue() + "_" + PermissionName.WRITE.getValue()))).isTrue();
    }

}
