package com.auth.application;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.provider.token.TokenStore;

import com.auth.AuthenticationMocks;
import com.auth.application.dto.LoginCredentials;
import com.auth.application.exception.EntityObjectNotFoundException;
import com.auth.application.exception.InvalidAttributeValueException;
import com.auth.application.feign.dto.AddUserRequest;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private TokenStore tokenStore;

    @Test
    void shouldRegister() throws InvalidAttributeValueException, EntityObjectNotFoundException {
        authService.register(new AddUserRequest(new LoginCredentials(), "em@em"));
        verify(userService, times(1)).addUser(Mockito.any(AddUserRequest.class));
    }

    @Test
    void shouldLogOut() throws InvalidAttributeValueException, EntityObjectNotFoundException {
        AuthenticationMocks.mockSecurityContextHolder();

        authService.logOut();
        verify(tokenStore, times(1)).removeAccessToken(Mockito.any());
    }

}
