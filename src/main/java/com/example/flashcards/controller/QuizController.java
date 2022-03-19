package com.example.flashcards.controller;

import com.example.flashcards.dto.quiz.*;
import com.example.flashcards.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/quizzes")
public class QuizController {
    private final QuizService quizService;

    @GetMapping()
    @ResponseStatus(OK)
    List<QuizDto> getAll(final Authentication authentication) {
        return quizService.getAll(authentication.getName());
    }

    @PostMapping
    @ResponseStatus(CREATED)
    void newQuiz(@RequestBody QuizCreateDto quizCreateDto, final Authentication authentication) {
        quizService.createQuiz(quizCreateDto, authentication.getName());
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    void editQuiz(@PathVariable final int id, @RequestBody final QuizEditDto quizEditDto, final Authentication authentication) {
        quizService.editQuiz(id, quizEditDto, authentication.getName());
    }

    //edit + solve w jedno

    @GetMapping("/{id}/edit")
    @ResponseStatus(OK)
    QuizDetailsDto getQuizDetailsToDisplayEdit(@PathVariable final int id, final Authentication authentication) {
        return quizService.getQuizDetails(id, authentication.getName());
    }

    @GetMapping("/{id}/solve")
    @ResponseStatus(OK)
    QuizDetailsDto getQuizDetailsToSolveQuiz(@PathVariable final int id, final Authentication authentication) {
        return quizService.getQuizDetails(id, authentication.getName());
    }

    @PostMapping("/{id}/solve")
    @ResponseStatus(CREATED)
    void solveQuiz(@PathVariable final int id, @RequestBody final List<QuizSolveDto> quizSolveDtos, final Authentication authentication) {
        quizService.solveQuiz(id, quizSolveDtos, authentication.getName());
    }

    @GetMapping("/{id}/results")
    @ResponseStatus(OK)
    QuizResultDto getResults(@PathVariable final int id, final Authentication authentication) {
        return quizService.getResults(id, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(OK)
    void deleteQuizById(@PathVariable final int id, final Authentication authentication) {
        quizService.deleteQuizById(id, authentication.getName());
    }
}
