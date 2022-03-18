package com.example.flashcards.dto.quiz;

import com.example.flashcards.model.Quiz;
import com.example.flashcards.model.QuizFlashcards;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.stream.Collectors;

@Value
@Data
@Builder
@RequiredArgsConstructor
public class  QuizDto {
    Integer id;
    String name;
    Integer score;
    String questionLangCode;
    String answerLangCode;

    public static QuizDto createFrom(final Quiz quiz){
        return QuizDto.builder()
                .id(quiz.getId())
                .name(quiz.getName())
                .score(quiz.getScore())
                .questionLangCode(quiz.getQuizFlashcards().get(0).getFlashcard().getQuestion().getLanguage().getLangCode())
                .answerLangCode(quiz.getQuizFlashcards().get(0).getFlashcard().getAnswer().getLanguage().getLangCode())
                .build();
    }

}


