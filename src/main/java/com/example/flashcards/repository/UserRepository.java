package com.example.flashcards.repository;

import com.example.flashcards.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findUserByUsernameOrEmail(final String username, final String email);

    Optional<User> findUserByUsername(final String username);

    void deleteByUsername(final String username);

}
