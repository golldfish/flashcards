package com.example.flashcards.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizSolvedDto {
    private Integer flashcardId;
    private String userAnswer;
}
