package com.auth.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByLogin(String login);

    Boolean existsByEmail(String email);

    Optional<User> findByLoginAndPassword(String login, String password);

    Optional<User> findByLogin(String login);

}
