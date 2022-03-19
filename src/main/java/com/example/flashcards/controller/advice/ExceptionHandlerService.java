package com.example.flashcards.controller.advice;

import com.example.flashcards.dto.ErrorResponse;
import com.example.flashcards.exception.BadRequestException;
import com.example.flashcards.exception.ConflictException;
import com.example.flashcards.exception.InvalidArgumentException;
import com.example.flashcards.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.example.flashcards.dto.ErrorResponse.buildErrorResponse;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ResponseBody
@ControllerAdvice
public class ExceptionHandlerService {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ErrorResponse badRequestHandler(final BadRequestException exception) {
        return buildErrorResponse(exception);
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse notFoundHandler(final NotFoundException exception) {
        return buildErrorResponse(exception);
    }

    @ResponseStatus(CONFLICT)
    @ExceptionHandler(ConflictException.class)
    public ErrorResponse conflictHandler(final ConflictException exception) {
        return buildErrorResponse(exception);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidArgumentException.class)
    public ErrorResponse invalidArgumentHandler(final InvalidArgumentException exception) {
        return buildErrorResponse(exception);
    }
}