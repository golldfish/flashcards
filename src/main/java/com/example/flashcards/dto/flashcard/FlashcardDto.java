package com.example.flashcards.dto.flashcard;

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

    public static FlashcardDto createFrom(final int flashcardId, final QuestionDto question, final AnswerDto answer){
        return FlashcardDto.builder()
                .id(flashcardId)
                .question(question)
                .answer(answer)
                .build();
    }
}
