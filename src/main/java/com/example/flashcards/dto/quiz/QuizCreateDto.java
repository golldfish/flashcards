package com.example.flashcards.dto.quiz;

import com.example.flashcards.dto.flashcard.AnswerDto;
import com.example.flashcards.dto.flashcard.FlashcardDto;
import com.example.flashcards.dto.flashcard.QuestionDto;
import com.example.flashcards.model.Flashcard;
import com.example.flashcards.model.Quiz;
import com.example.flashcards.model.QuizFlashcards;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.List;
import java.util.stream.Collectors;

@Value
@Data
@Builder
@RequiredArgsConstructor
public class QuizCreateDto {

    String name;
    String questionLangCode;
    String answerLangCode;
    List<Integer> flashcardsIds;

    public static QuizCreateDto createFrom(final Quiz quiz){
        return QuizCreateDto.builder()
                .name(quiz.getName())
                .questionLangCode(quiz.getQuizFlashcards().get(0).getFlashcard().getQuestion().getLanguage().getLangCode())
                .answerLangCode(quiz.getQuizFlashcards().get(0).getFlashcard().getAnswer().getLanguage().getLangCode())
                .flashcardsIds(quiz.getQuizFlashcards().stream().map(QuizFlashcards::getId).collect(Collectors.toList()))
                .build();
    }}