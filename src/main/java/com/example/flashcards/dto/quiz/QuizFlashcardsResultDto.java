package com.example.flashcards.dto.quiz;

import com.example.flashcards.dto.flashcard.QuestionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizFlashcardsResultDto {

    private Integer id;
    private QuestionDto question;
    private String correctAnswer;
    private String userAnswer;
}
