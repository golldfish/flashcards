package com.example.flashcards.dto.quiz;

import com.example.flashcards.model.Quiz;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Data
@Builder
@RequiredArgsConstructor
public class QuizDto {
    Integer id;
    String name;
    Integer score;

    public static QuizDto createFrom(final Quiz quiz){
        return QuizDto.builder()
                .id(quiz.getId())
                .name(quiz.getName())
                .score(quiz.getScore())
                .build();
    }
}
