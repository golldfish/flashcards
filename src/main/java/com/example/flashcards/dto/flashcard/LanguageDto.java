package com.example.flashcards.dto.flashcard;

import com.example.flashcards.model.Language;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Builder
@Value
@RequiredArgsConstructor
public class LanguageDto {
    String langCode;
    String name;

    public static LanguageDto createFrom(final Language language){
        return LanguageDto.builder()
                .langCode(language.getLangCode())
                .name(language.getName())
                .build();
    }
}
