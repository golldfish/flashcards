package com.example.flashcards.dto.flashcard;

import com.example.flashcards.model.Question;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Data
@Builder
@RequiredArgsConstructor
public class QuestionDto {

    String value;
    String langCode;

    public static QuestionDto createFrom(final Question question) {
        return QuestionDto.builder()
                .langCode(question.getLanguage().getLangCode())
                .value(question.getValue())
                .build();
    }
}

