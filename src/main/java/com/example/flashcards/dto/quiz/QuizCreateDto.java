package com.example.flashcards.dto.quiz;

import com.example.flashcards.model.Quiz;
import com.example.flashcards.model.QuizFlashcards;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
@Data
@Builder
@RequiredArgsConstructor
public class QuizCreateDto {

    String name;
    String questionLangCode;
    String answerLangCode;
    List<Integer> flashcardsIds;

    public static QuizCreateDto createFrom(final Quiz quiz){
        return QuizCreateDto.builder()
                .name(quiz.getName())
                .questionLangCode(quiz.getQuizFlashcards().get(0).getFlashcard().getQuestion().getLanguage().getLangCode())
                .answerLangCode(quiz.getQuizFlashcards().get(0).getFlashcard().getAnswer().getLanguage().getLangCode())
                .flashcardsIds(quiz.getQuizFlashcards().stream().map(QuizFlashcards::getId).collect(Collectors.toList()))
                .build();
    }
}