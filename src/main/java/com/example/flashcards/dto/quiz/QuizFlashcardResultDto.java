package com.example.flashcards.dto.quiz;

import com.example.flashcards.dto.flashcard.QuestionDto;
import com.example.flashcards.model.QuizFlashcard;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Data
@Builder
@RequiredArgsConstructor
public class QuizFlashcardResultDto {
    Integer id;
    QuestionDto question;
    String correctAnswer;
    String userAnswer;

    public static QuizFlashcardResultDto createFrom(final QuizFlashcard quizFlashcard) {
        return QuizFlashcardResultDto.builder()
                .id(quizFlashcard.getFlashcard().getId())
                .question(QuestionDto.createFrom(quizFlashcard.getFlashcard().getQuestion()))
                .correctAnswer(quizFlashcard.getFlashcard().getAnswer().getValue())
                .userAnswer(quizFlashcard.getUserAnswer())
                .build();
    }
}
