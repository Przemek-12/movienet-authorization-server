package com.auth.infrastructure.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.auth.application.UserService;
import com.auth.application.exception.EntityObjectNotFoundException;
import com.auth.domain.Role;
import com.auth.domain.User;

@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = getUser(login);
        Set<? extends GrantedAuthority> authorities = getAuthorities(user.getRoles());
        return new UserDetailsImpl(user, authorities);
    }

    private User getUser(String login) {
        try {
            return userService.getEntityByLogin(login);
        } catch (EntityObjectNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    private Set<? extends GrantedAuthority> getAuthorities(Set<Role> roles) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        roles.forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName().getValue()));
            role.getPermissions().forEach(
                    permission -> authorities.add(new SimpleGrantedAuthority(permission.getName().getValue())));
        });
        return authorities;
    }

}
