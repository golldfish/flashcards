package com.example.flashcards.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

@Value
@Builder
@RequiredArgsConstructor
public class ErrorResponse {

    @JsonProperty(value = "error_message")
    String message;

    public static ErrorResponse buildErrorResponse(final Exception exception) {
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .build();
    }

    public static ErrorResponse buildErrorResponse(final List<FieldError> fieldErrors) {
        return ErrorResponse.builder()
                .message(fieldErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList()).toString())
                .build();
    }
}
