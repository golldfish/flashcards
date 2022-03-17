package com.example.flashcards.dto.flashcard;

import com.example.flashcards.model.Answer;
import com.example.flashcards.model.Question;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Data
@Builder
@RequiredArgsConstructor
public class AnswerDto {

    String value;
    String langCode;

    public static AnswerDto createFrom(final Answer answer) {
        return AnswerDto.builder()
                .langCode(answer.getLanguage().getLangCode())
                .value(answer.getValue())
                .build();
    }
}

