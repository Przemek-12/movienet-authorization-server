package com.auth.domain.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Long id;

    @Enumerated(EnumType.STRING)
    @NonNull
    @NotNull
    @Getter
    private RoleName name;

    @ManyToMany(fetch = FetchType.EAGER)
    @Getter
    private Set<Permission> permissions = new HashSet<>();
    
    private Role(@NonNull RoleName name, @NonNull Set<Permission> permissions) {
        this.name=name;
        this.permissions.addAll(permissions);
    }

    public static Role create(@NonNull RoleName name, @NonNull Set<Permission> permissions) {
        return new Role(name, permissions);
    }
}

