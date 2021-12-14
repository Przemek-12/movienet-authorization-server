package com.auth.application;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth.application.dto.AddUserResult;
import com.auth.application.dto.AddUserResult.ErrorMessage;
import com.auth.application.dto.AddUserResult.Property;
import com.auth.application.dto.LoginCredentials;
import com.auth.application.exception.EntityObjectNotFoundException;
import com.auth.application.exception.InvalidAttributeValueException;
import com.auth.application.feign.dto.AddUserRequest;
import com.auth.application.feign.dto.LoginAndPassword;
import com.auth.domain.entity.Role;
import com.auth.domain.entity.User;
import com.auth.domain.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<AddUserResult> addUser(AddUserRequest request)
            throws InvalidAttributeValueException, EntityObjectNotFoundException {
        LoginAndPassword loginAndPassword = decodeLoginAndPassword(request.getLoginCredentials());
        AddUserResult result = new AddUserResult();
        checkIfUserAlreadyExists(result, loginAndPassword.getLogin(), request.getEmail());
        if (result.allValid()) {
            addUser(loginAndPassword, request);
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
    }

    public User getEntityByLogin(String login) throws EntityObjectNotFoundException {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityObjectNotFoundException(User.class.getSimpleName()));
    }

    public void deleteUser(Long userId) throws EntityObjectNotFoundException {
        checkIfUserExistsById(userId);
        userRepository.deleteById(userId);
    }

    private LoginAndPassword decodeLoginAndPassword(LoginCredentials loginCredentials) {
        String decodedLoginAndPassword = new String(Base64.getDecoder().decode(loginCredentials.getLoginCredentials()));
        String[] creadentials = decodedLoginAndPassword.split(":");
        String login = creadentials[0];
        String password = passwordEncoder.encode(creadentials[1]);
        return new LoginAndPassword(login, password);
    }

    private void checkIfUserAlreadyExists(AddUserResult result, String login, String email) {
        if (userExistsByLogin(login)) {
            result.addError(Property.LOGIN, ErrorMessage.ALREADY_TAKEN);
        }
        if (userExistsByEmail(email)) {
            result.addError(Property.EMAIL, ErrorMessage.ALREADY_TAKEN);
        }
    }

    private Boolean userExistsByLogin(String login) {
        return userRepository.existsByLogin(login);
    }

    private Boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private void addUser(LoginAndPassword loginAndPassword, AddUserRequest request)
            throws InvalidAttributeValueException, EntityObjectNotFoundException {
        User user = User.create(
                loginAndPassword.getLogin(),
                loginAndPassword.getPassword(),
                request.getEmail(),
                getInitialRoles());
        userRepository.save(user);
    }

    private Set<Role> getInitialRoles() throws EntityObjectNotFoundException {
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.getUserRole());
        return roles;
    }

    private void checkIfUserExistsById(Long userId) throws EntityObjectNotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new EntityObjectNotFoundException(User.class.getSimpleName());
        }
    }

}
