package com.example.flashcards.validation;

import com.example.flashcards.dto.flashcard.LanguageDto;
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
public class LanguageValidator {

    private static final int MIN_NAME_LENGTH = 1;
    private static final int MAX_NAME_LENGTH = 32;
    private static final int LANG_CODE_LENGTH = 3;

    public void validateLanguageParameters(final LanguageDto languageDto, final boolean isEdit) {
        final List<FieldError> fieldErrorList = getFieldErrors(languageDto, isEdit);

        if (!fieldErrorList.isEmpty()) {
            throw new InvalidArgumentException(fieldErrorList);
        }
    }

    private List<FieldError> getFieldErrors(final LanguageDto languageDto, final boolean isEdit) {
        if (isEdit) {
            return Optional.ofNullable(languageDto).map(lang -> Stream.of(checkName(lang.getName()))
                    .filter(Objects::nonNull).collect(Collectors.toList())).orElse(Collections.emptyList());
        } else {
            return Optional.ofNullable(languageDto).map(languageCreate -> Stream.of(checkName(languageCreate.getName()),
                            checkLangCode(languageCreate.getLangCode()))
                    .filter(Objects::nonNull).collect(Collectors.toList())).orElse(Collections.emptyList());
        }
    }

    private FieldError checkName(final String name) {
        if (StringUtils.isBlank(name) || name.length() > MAX_NAME_LENGTH || name.length() < MIN_NAME_LENGTH) {
            final String message = "Name cannot be blank or null and must be in range 3-32 characters";
            return new FieldError("String", "name", name, false, null, null, message);
        } else {
            return null;
        }
    }

    private FieldError checkLangCode(final String langCode) {
        if (StringUtils.isBlank(langCode) || langCode.length() != LANG_CODE_LENGTH) {
            final String message = "Lang code cannot be blank or null and must have 3 characters";
            return new FieldError("String", "langCode", langCode, false, null, null, message);
        } else {
            return null;
        }
    }

}


