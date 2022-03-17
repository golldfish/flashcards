package com.example.flashcards.repository;

import com.example.flashcards.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {}
