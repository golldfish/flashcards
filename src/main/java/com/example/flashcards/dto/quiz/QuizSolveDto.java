package com.example.flashcards.dto.quiz;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Data
@Builder
@RequiredArgsConstructor
public class QuizSolveDto {
    Integer flashcardId;
    String userAnswer;

}
