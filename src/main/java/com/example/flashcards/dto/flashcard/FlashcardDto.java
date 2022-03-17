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
public class FlashcardDto {

    Integer id;
    QuestionDto question;
    AnswerDto answer;

    public static FlashcardDto createFrom(final Flashcard flashcard){
        return FlashcardDto.builder()
                .id(flashcard.getId())
                .question(QuestionDto.createFrom(flashcard.getQuestion()))
                .answer(AnswerDto.createFrom(flashcard.getAnswer()))
                .build();
    }
}
