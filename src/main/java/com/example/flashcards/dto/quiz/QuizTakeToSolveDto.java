package com.example.flashcards.dto.quiz;

import com.example.flashcards.dto.flashcard.FlashcardForQuizDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizTakeToSolveDto {

    private Integer id;
    private String name;
    private String questionLangCode;
    private String answerLangCode;
    private List<FlashcardForQuizDto> flashcards;
}
