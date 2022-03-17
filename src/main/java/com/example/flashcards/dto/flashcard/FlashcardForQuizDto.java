package com.example.flashcards.dto.flashcard;

import com.example.flashcards.model.Flashcard;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Data
@Builder
@RequiredArgsConstructor
public class FlashcardForQuizDto {
    Integer id;
    QuestionDto question;

    public static FlashcardForQuizDto createFrom(final Flashcard flashcard){
        return FlashcardForQuizDto.builder()
                .id(flashcard.getId())
                .question(QuestionDto.createFrom(flashcard.getQuestion()))
                .build();
    }
}
