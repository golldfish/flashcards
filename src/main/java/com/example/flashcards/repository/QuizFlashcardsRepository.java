package com.example.flashcards.repository;

import com.example.flashcards.model.QuizFlashcard;
import com.example.flashcards.model.QuizFlashcardsKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.Set;

public interface QuizFlashcardsRepository extends CrudRepository<QuizFlashcard, QuizFlashcardsKey> {

    Set<QuizFlashcard> findByQuizId(final Integer id);

    @Query(value = "SELECT f.id FROM QuizFlashcard qf JOIN Flashcard f ON qf.flashcard = f WHERE qf.quiz.id = :id")
    Set<Integer> findFlashcardsIdByQuizId(final Integer id);

    Optional<QuizFlashcard> findByQuizIdAndFlashcardId(final Integer quizId, final Integer flashcardId);

    Set<QuizFlashcard> findByFlashcardId(final int id);

    void deleteById(final QuizFlashcardsKey key);
}
