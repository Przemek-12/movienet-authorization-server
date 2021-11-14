package com.auth.application;

import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.auth.application.dto.AddUserResult;
import com.auth.application.dto.LoginCredentials;
import com.auth.application.exception.EntityObjectNotFoundException;
import com.auth.application.exception.InvalidAttributeValueException;
import com.auth.application.exception.UserNotFoundException;
import com.auth.application.feign.dto.AddUserRequest;
import com.auth.application.feign.dto.LoginAndPassword;
import com.auth.application.feign.dto.UserDTO;
import com.auth.domain.Role;
import com.auth.domain.User;
import com.auth.domain.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;

    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
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

    public UserDTO getUser(LoginCredentials loginCredentials) throws UserNotFoundException {
        LoginAndPassword loginAndPassword = decodeLoginAndPassword(loginCredentials);
        return mapToDTO(findByLoginAndPassword(loginAndPassword.getLogin(), loginAndPassword.getPassword()));
    }

    public UserDTO getUser(Long id) throws UserNotFoundException {
        return mapToDTO(findById(id));
    }

    public User getEntityByLogin(String login) throws EntityObjectNotFoundException {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityObjectNotFoundException(User.class.getSimpleName()));
    }

    private LoginAndPassword decodeLoginAndPassword(LoginCredentials loginCredentials) {
        String decodedLoginAndPassword = new String(Base64.getDecoder().decode(loginCredentials.getLoginCredentials()));
        String[] creadentials = decodedLoginAndPassword.split(":");
        return new LoginAndPassword(creadentials[0], creadentials[1]);
    }

    private void checkIfUserAlreadyExists(AddUserResult result, String login, String email) {
        if (userExistsByLogin(login)) {
            result.invalidLogin();
        }
        if (userExistsByEmail(email)) {
            result.invalidEmail();
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

    private User findById(Long userId) throws UserNotFoundException {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    private User findByLoginAndPassword(String login, String password) throws UserNotFoundException {
        return userRepository.findByLoginAndPassword(login, password).orElseThrow(UserNotFoundException::new);
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .login(user.getLogin())
                .email(user.getEmail())
                .build();
    }

}
