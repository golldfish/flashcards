package com.example.flashcards.validation;

import com.example.flashcards.dto.flashcard.FlashcardDto;
import com.example.flashcards.exception.InvalidArgumentException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class FlashcardValidator {

    private static final int MIN_NAME_LENGTH = 1;
    private static final int MAX_NAME_LENGTH = 32;
    private static final int LANG_CODE_LENGTH = 3;

    public void validateFlashcardParameters(final FlashcardDto flashcardDto) {
        final List<FieldError> fieldErrorList = getFieldErrors(flashcardDto);

        if (!fieldErrorList.isEmpty()) {
            throw new InvalidArgumentException(fieldErrorList);
        }
    }

    private List<FieldError> getFieldErrors(final FlashcardDto flashcardDto) {

        return Optional.ofNullable(flashcardDto).map(flashcard -> Stream.of(checkValue(flashcard.getQuestion().getValue()),
                        checkLangCode(flashcard.getQuestion().getLangCode()),
                        checkValue(flashcard.getAnswer().getValue()),
                        checkLangCode(flashcard.getAnswer().getLangCode()))
                .filter(Objects::nonNull).collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    private FieldError checkValue(final String value) {
        if (StringUtils.isBlank(value) || value.length() > MAX_NAME_LENGTH || value.length() < MIN_NAME_LENGTH) {
            final String message = "Invalid name";
            return new FieldError("String", "value", value, false, null, null, message);
        } else {
            return null;
        }
    }

    private FieldError checkLangCode(final String langCode) {
        if (StringUtils.isBlank(langCode) || langCode.length() != LANG_CODE_LENGTH) {
            final String message = "Invalid langCode";
            return new FieldError("String", "langCode", langCode, false, null, null, message);
        } else {
            return null;
        }
    }

}


