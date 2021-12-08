package com.auth.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth.application.exception.EntityObjectNotFoundException;
import com.auth.domain.entity.Role;
import com.auth.domain.entity.RoleName;
import com.auth.domain.repository.RoleRepository;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role getUserRole() throws EntityObjectNotFoundException {
        return roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new EntityObjectNotFoundException(Role.class.getSimpleName()));
    }

}
