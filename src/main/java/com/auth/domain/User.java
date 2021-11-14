package com.auth.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.auth.application.exception.InvalidAttributeValueException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Long id;

    @NotNull
    @NonNull
    @Getter
    @Column(unique = true)
    private String login;

    @NotNull
    @NonNull
    @Getter
    private String password;

    @NotNull
    @NonNull
    @Getter
    @Column(unique = true)
    @Email
    private String email;

    @NotEmpty
    @ManyToMany(fetch = FetchType.EAGER)
    @Getter
    private Set<Role> roles = new HashSet<>();

    public User(@NonNull String login, @NonNull String password,
            @NonNull String email, Set<Role> roles) throws InvalidAttributeValueException {
        checkIfRolesAreValid(roles);
        this.login = login;
        this.password = password;
        this.email = email;
        this.roles.addAll(roles);
    }

    public static User create(String login, String password, String email, Set<Role> roles)
            throws InvalidAttributeValueException {
        return new User(login, password, email, roles);
    }

    private void checkIfRolesAreValid(Set<Role> roles) throws InvalidAttributeValueException {
        if (roles.isEmpty()) {
            throw new InvalidAttributeValueException();
        }
    }

}
