package com.example.flashcards.dto.quiz;

import com.example.flashcards.model.Quiz;
import com.example.flashcards.model.QuizFlashcard;
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
public class QuizResultDto {
    QuizDto quizData;
    Set<QuizFlashcardResultDto> flashcards;

    public static QuizResultDto createFrom(final Quiz quiz, final Set<QuizFlashcard> flashcards) {
        return QuizResultDto.builder()
                .quizData(QuizDto.createFrom(quiz))
                .flashcards(flashcards.stream().map(QuizFlashcardResultDto::createFrom).collect(Collectors.toSet()))
                .build();
    }

}
