package com.example.flashcards.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.FieldError;

import java.util.List;

@AllArgsConstructor
@Getter
public class InvalidArgumentException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Invalid argument";

    private final List<FieldError> fieldErrors;

//
//    public InvalidArgumentException(final List<FieldError> fieldErrors) {
//        this.fieldErrors = fieldErrors;
//    }
}
