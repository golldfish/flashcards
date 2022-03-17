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
public class QuizEditDisplayDto {
    private Integer id;
    private String name;
    private Integer score;
    private String questionLangCode;
    private String answerLangCode;
    private List<FlashcardForQuizDto> flashcards;
}

//TO REMOVE
