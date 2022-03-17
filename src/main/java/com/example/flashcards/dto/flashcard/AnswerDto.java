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
public class AnswerDto {

    String name;
    String langCode;

    public static AnswerDto createFrom(final Question question) {
        return AnswerDto.builder()
                .langCode(question.getLanguage().getLangCode())
                .name(question.getValue())
                .build();
    }
}

