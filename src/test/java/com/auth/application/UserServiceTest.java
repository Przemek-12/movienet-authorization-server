package com.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.auth.UserMocks;
import com.auth.application.dto.AddUserResult;
import com.auth.application.dto.AddUserResult.Property;
import com.auth.application.exception.EntityObjectNotFoundException;
import com.auth.application.exception.InvalidAttributeValueException;
import com.auth.application.kafka.KafkaMessageProducer;
import com.auth.domain.entity.User;
import com.auth.domain.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private KafkaMessageProducer kafkaMessageProducer;

    private void whenUserRepositoryFindByLoginThenReturnEmptyOptional() {
        when(userRepository.findByLogin(Mockito.anyString()))
                .thenReturn(Optional.empty());
    }

    private void whenUserRepositoryFindByLoginThenReturnUserMock()
            throws JsonMappingException, JsonProcessingException, InvalidAttributeValueException {
        when(userRepository.findByLogin(Mockito.anyString()))
                .thenReturn(Optional.of(UserMocks.userMock()));
    }

    private void whenUserRepositoryExistsByLoginThenReturn(boolean bool)
            throws JsonMappingException, JsonProcessingException, InvalidAttributeValueException {
        when(userRepository.existsByLogin(Mockito.anyString()))
                .thenReturn(bool);
    }

    private void whenUserRepositoryExistsByEmailThenReturn(boolean bool)
            throws JsonMappingException, JsonProcessingException, InvalidAttributeValueException {
        when(userRepository.existsByEmail(Mockito.anyString()))
                .thenReturn(bool);
    }

    private void whenPasswordEncoderEncodeThenReturnPassword()
            throws JsonMappingException, JsonProcessingException, InvalidAttributeValueException {
        when(passwordEncoder.encode(Mockito.anyString()))
                .thenReturn("pass");
    }

    private void userExistsById(boolean bool) {
        when(userRepository.existsById(Mockito.anyLong()))
                .thenReturn(bool);
    }

    @Test
    void shouldAddUser() throws JsonMappingException, JsonProcessingException, InvalidAttributeValueException,
            EntityObjectNotFoundException {
        whenPasswordEncoderEncodeThenReturnPassword();
        whenUserRepositoryExistsByLoginThenReturn(false);
        whenUserRepositoryExistsByEmailThenReturn(false);
        
        assertThat(userService.addUser(UserMocks.addUserRequestMock()).getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(userRepository, times(1)).save(Mockito.any(User.class));
        verify(kafkaMessageProducer, times(1)).sendMessage(Mockito.eq("user-added"), Mockito.anyString());
    }

    @Test
    void shouldNotAddUserWhenInvalidEmail()
            throws JsonMappingException, JsonProcessingException, InvalidAttributeValueException,
            EntityObjectNotFoundException {
        whenPasswordEncoderEncodeThenReturnPassword();
        whenUserRepositoryExistsByLoginThenReturn(false);
        whenUserRepositoryExistsByEmailThenReturn(true);

        ResponseEntity<AddUserResult> response = userService.addUser(UserMocks.addUserRequestMock());

        assertThat(response.getBody().getErrors().containsKey(Property.EMAIL.getValue())).isTrue();
        assertThat(response.getBody().getErrors()).hasSize(1);
        verify(userRepository, never()).save(Mockito.any(User.class));
    }

    @Test
    void shouldNotAddUserWhenInvalidLogin()
            throws JsonMappingException, JsonProcessingException, InvalidAttributeValueException,
            EntityObjectNotFoundException {
        whenPasswordEncoderEncodeThenReturnPassword();
        whenUserRepositoryExistsByLoginThenReturn(true);
        whenUserRepositoryExistsByEmailThenReturn(false);

        ResponseEntity<AddUserResult> response = userService.addUser(UserMocks.addUserRequestMock());

        assertThat(response.getBody().getErrors().containsKey(Property.LOGIN.getValue())).isTrue();
        assertThat(response.getBody().getErrors()).hasSize(1);
        verify(userRepository, never()).save(Mockito.any(User.class));
    }

    @Test
    void shouldGetEntityByLogin() throws EntityObjectNotFoundException, JsonMappingException, JsonProcessingException,
            InvalidAttributeValueException {
        whenUserRepositoryFindByLoginThenReturnUserMock();
        assertThat(userService.getEntityByLogin("login")).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhneEntityNotFound() {
        whenUserRepositoryFindByLoginThenReturnEmptyOptional();

        assertThrows(EntityObjectNotFoundException.class,
                () -> userService.getEntityByLogin("login"));
    }

    @Test
    void shouldDeleteUser() throws EntityObjectNotFoundException {
        userExistsById(true);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
        verify(kafkaMessageProducer, times(1)).sendMessage(Mockito.eq("user-deleted"), Mockito.eq("1"));
    }

    @Test
    void shouldNotDeleteUserIfNotExists() {
        userExistsById(false);

        assertThrows(EntityObjectNotFoundException.class,
                () -> userService.deleteUser(1L));
        verify(userRepository, never()).deleteById(1L);
        verify(kafkaMessageProducer, never()).sendMessage(Mockito.eq("user-deleted"), Mockito.anyString());
    }
}
