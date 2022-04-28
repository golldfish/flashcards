package com.example.flashcards.dto.quiz;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.Set;

@Value
@Data
@Builder
@RequiredArgsConstructor
public class QuizCreateDto {

    String name;
    Set<Integer> flashcardsId;

}

