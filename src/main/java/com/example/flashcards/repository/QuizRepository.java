package com.example.flashcards.repository;

import com.example.flashcards.model.Quiz;
import com.example.flashcards.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Integer> {

    List<Quiz> findAllByUser(final User user);

}
