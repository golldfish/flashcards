package com.example.flashcards.controller.advice;

import com.example.flashcards.dto.ErrorResponse;
import com.example.flashcards.exception.BadRequestException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.example.flashcards.dto.ErrorResponse.buildErrorResponse;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseBody
@ControllerAdvice
public class ExceptionHandlerService {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(BadRequestException.class)
    public ErrorResponse badRequestHandler(final BadRequestException exception) {
        return buildErrorResponse(exception);
    }

}