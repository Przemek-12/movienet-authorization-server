package com.auth.application;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.auth.application.dto.AddUserResult;
import com.auth.application.dto.AuthInfo;
import com.auth.application.exception.EntityObjectNotFoundException;
import com.auth.application.exception.InvalidAttributeValueException;
import com.auth.application.feign.dto.AddUserRequest;

@Service
public class AuthService {

    private final UserService userService;
    private final TokenStore tokenStore;

    @Autowired
    public AuthService(UserService userService, TokenStore tokenStore) {
        this.userService = userService;
        this.tokenStore = tokenStore;
    }

    public ResponseEntity<AddUserResult> register(@RequestBody AddUserRequest addUserRequest)
            throws InvalidAttributeValueException, EntityObjectNotFoundException {
        return userService.addUser(addUserRequest);
    }

    public void logOut() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        tokenStore.removeAccessToken((DefaultOAuth2AccessToken) a.getCredentials());
    }

    public AuthInfo getAuthInfo() {
        Set<String> roles = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toSet());

        return AuthInfo.builder()
                .roles(roles)
                .build();
    }
}
