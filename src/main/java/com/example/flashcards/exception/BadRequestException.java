package com.example.flashcards.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException(final String message) {
        super(message);
    }
}
