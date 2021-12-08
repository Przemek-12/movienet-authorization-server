package com.auth;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticationMocks {

    public static void mockSecurityContextHolder() {
        SecurityContextHolder.setContext(new SecurityContext() {

            private static final long serialVersionUID = 1L;

            @Override
            public Authentication getAuthentication() {
                return new Authentication() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public String getName() {
                        return null;
                    }

                    @Override
                    public Collection<? extends GrantedAuthority> getAuthorities() {
                        return null;
                    }

                    @Override
                    public Object getCredentials() {
                        return null;
                    }

                    @Override
                    public Object getDetails() {
                        return null;
                    }

                    @Override
                    public Object getPrincipal() {
                        return null;
                    }

                    @Override
                    public boolean isAuthenticated() {
                        return false;
                    }

                    @Override
                    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
                    }

                };
            }

            @Override
            public void setAuthentication(Authentication authentication) {
            }

        });
    }
}
