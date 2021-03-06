package com.example.flashcards.repository;

import com.example.flashcards.model.Flashcard;
import com.example.flashcards.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FlashcardRepository extends JpaRepository<Flashcard, Integer> {

    List<Flashcard> findAllByUser(final User user);

    Optional<Flashcard> findByUserUsernameAndQuestionValue (final String username, final String questionValue);

    List<Flashcard> findAllByQuestionLanguageLangCode(final String langCode);

    List<Flashcard> findAllByAnswerLanguageLangCode(final String langCode);

    List<Flashcard> findAllByUserAndQuestionLanguageLangCodeAndAnswerLanguageLangCode(final User user,
                                                                                      final String questionLangCode,
                                                                                      final String answerLangCode);

    List<Flashcard> findAllByUserAndQuestionLanguageLangCode(final User user, final String questionLangCode);

    List<Flashcard> findAllByUserAndAnswerLanguageLangCode(final User user, final String answerLangCode);

    @Query(value = "select f from Flashcard  f JOIN QuizFlashcard qf ON f.id = qf.flashcard.id where qf.quiz.id=:id")
    Set<Flashcard> findAllByQuizId(final Integer id);

    @Query(value = "SELECT f FROM Flashcard f WHERE f.user=:user AND f.question.value LIKE %:query%")
    List<Flashcard> findAllByUserAndQuestionContaining(@Param("user") final User user,
                                                       @Param("query") final String query);

    @Query(value = "SELECT f FROM Flashcard f WHERE f.user=:user AND f.question.value LIKE %:query% AND f.question" +
            ".language.langCode=:questionLangCode AND f.answer.language.langCode=:answerLangCode")
    List<Flashcard> findAllByUserAndQuestionLangCodeAndAnswerLangCodeAndQuestionContaining(
            @Param("user") final User user, @Param("query") final String query,
            @Param("questionLangCode") final String questionLangCode,
            @Param("answerLangCode") final String answerLangCode);

    @Query(value = "SELECT f FROM Flashcard f WHERE f.user=:user AND f.question.value LIKE %:query% AND f.question" +
                   ".language.langCode=:questionLangCode")
    List<Flashcard> findAllByUserAndQuestionLangCodeAndQuestionContaining(@Param("user") final User user,
                                                                          @Param("query") final String query,
                                                                          @Param("questionLangCode")
                                                                          final String questionLangCode);

    @Query(value = "SELECT f FROM Flashcard f WHERE f.user=:user AND f.question.value LIKE %:query% AND f.answer.language.langCode=:answerLangCode")
    List<Flashcard> findAllByUserAndAnswerLangCodeAndQuestionContaining(@Param("user") final User user,
                                                                        @Param("query") final String query,
                                                                        @Param("answerLangCode")
                                                                        final String answerLangCode);

}
