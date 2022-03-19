package com.example.flashcards.dto.quiz;

import com.example.flashcards.model.Flashcard;
import com.example.flashcards.model.Quiz;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Set;
import java.util.stream.Collectors;

@Value
@Data
@Builder
@RequiredArgsConstructor
public class QuizDetailsDto {
    QuizDto quizData;
    Set<QuizFlashcardDto> flashcards;

    public static QuizDetailsDto createFrom(final Quiz quiz, final Set<Flashcard> flashcards) {
        return QuizDetailsDto.builder()
                .quizData(QuizDto.createFrom(quiz))
                .flashcards(flashcards.stream().map(QuizFlashcardDto::createFrom).collect(Collectors.toSet()))
                .build();
    }
}
