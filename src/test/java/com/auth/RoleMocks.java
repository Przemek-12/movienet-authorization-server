package com.auth;

import java.util.Set;

import org.json.JSONException;

import com.auth.domain.entity.Permission;
import com.auth.domain.entity.PermissionName;
import com.auth.domain.entity.Role;
import com.auth.domain.entity.RoleName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoleMocks {

    public static Role userRoleMock() throws JsonMappingException, JsonProcessingException {
        return Role.create(RoleName.ROLE_USER, Set.of());
    }

    public static Role roleMock(RoleName name, Set<Permission> permissions)
            throws JsonMappingException, JsonProcessingException, JSONException {
        return Role.create(name, permissions);
    }

    public static Permission permissionMock(PermissionName name)
            throws JsonMappingException, JsonProcessingException, JSONException {
        return Permission.create(name);
    }

}
