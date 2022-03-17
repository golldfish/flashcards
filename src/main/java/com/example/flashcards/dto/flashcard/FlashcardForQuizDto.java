package com.example.flashcards.dto.flashcard;

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

    public static FlashcardForQuizDto createFrom(final int flashcardId, final QuestionDto question){
        return FlashcardForQuizDto.builder()
                .id(flashcardId)
                .question(question)
                .build();
    }
}
