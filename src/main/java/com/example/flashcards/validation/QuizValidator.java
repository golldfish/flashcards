package com.example.flashcards.validation;

import com.example.flashcards.dto.quiz.QuizCreateDto;
import com.example.flashcards.dto.quiz.QuizEditDto;
import com.example.flashcards.dto.quiz.QuizSolveDto;
import com.example.flashcards.exception.InvalidArgumentException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class QuizValidator {

    private static final int MIN_NAME_LENGTH = 3;
    private static final int MAX_NAME_LENGTH = 32;
    private static final int LANG_CODE_LENGTH = 3;

    public void validateQuizCreateParameters(final QuizCreateDto quizCreateDto) {
        final List<FieldError> fieldErrorList = getFieldErrors(quizCreateDto);

        if (!fieldErrorList.isEmpty()) {
            throw new InvalidArgumentException(fieldErrorList);
        }
    }

    public void validateQuizEditParameters(final QuizEditDto quizEditDto) {
        final List<FieldError> fieldErrorList = getFieldErrors(quizEditDto);

        if (!fieldErrorList.isEmpty()) {
            throw new InvalidArgumentException(fieldErrorList);
        }
    }

    public void validateQuizSolveParameters(final QuizSolveDto quizSolveDto) {
        final List<FieldError> fieldErrorList = getFieldErrors(quizSolveDto);

        if (!fieldErrorList.isEmpty()) {
            throw new InvalidArgumentException(fieldErrorList);
        }
    }

    private List<FieldError> getFieldErrors(final QuizCreateDto quizCreateDto) {

        return Optional.ofNullable(quizCreateDto).map(quizCreate -> Stream.of(checkName(quizCreate.getName()),
                        checkFlashcardsId(quizCreate.getFlashcardsId()))
                .filter(Objects::nonNull).collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    private List<FieldError> getFieldErrors(final QuizEditDto quizEditDto) {

        return Optional.ofNullable(quizEditDto).map(quizEdit -> Stream.of(checkName(quizEdit.getName()),
                        checkFlashcardsId(quizEdit.getFlashcardsId()))
                .filter(Objects::nonNull).collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    private List<FieldError> getFieldErrors(final QuizSolveDto quizSolveDto) {

        return Optional.ofNullable(quizSolveDto).map(qs -> Stream.of(checkFlashcardId(qs.getFlashcardId()))
                .filter(Objects::nonNull).collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    private FieldError checkName(final String name) {
        if (StringUtils.isBlank(name) || name.length() > MAX_NAME_LENGTH || name.length() < MIN_NAME_LENGTH) {
            final String message = "Invalid quiz name";
            return new FieldError("String", "name", name, false, null, null, message);
        } else {
            return null;
        }
    }

    private FieldError checkFlashcardsId(final Set<Integer> flashcardsId) {
        if (flashcardsId.isEmpty()) {
            final String message = "Flashcards ids needs to be provided.";
            return new FieldError("String", "flashcardsId", flashcardsId, false, null, null, message);
        } else {
            return null;
        }
    }

    private FieldError checkFlashcardId(final Integer flashcardId) {
        if (!nonNull(flashcardId)) {
            final String message = "Flashcard id needs to be provided.";
            return new FieldError("Integer", "flashcardId", flashcardId, false, null, null, message);
        } else {
            return null;
        }
    }

}
