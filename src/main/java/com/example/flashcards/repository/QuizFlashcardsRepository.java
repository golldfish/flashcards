package com.example.flashcards.repository;

import com.example.flashcards.model.QuizFlashcards;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizFlashcardsRepository extends JpaRepository<QuizFlashcards, Integer> {

    List<QuizFlashcards> findByQuizId(final Integer id);

    List<QuizFlashcards> findByQuizId(final Integer id, final Sort sort);

    Optional<QuizFlashcards> findByQuizIdAndFlashcardId(final Integer quizId, final Integer flashcardId);
}
