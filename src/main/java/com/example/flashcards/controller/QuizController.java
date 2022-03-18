package com.example.flashcards.controller;

import com.flashcards.dto.quiz.*;
import com.flashcards.exception.AlreadyExistsException;
import com.flashcards.service.QuizService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/quizzes")
public class QuizController {
    private final QuizService quizService;

    @GetMapping()
    @ResponseStatus(OK)
    ResponseEntity<Map<String, Object>> getAll(final Authentication authentication) {
        return new ResponseEntity<>(quizService.getQuizzes(  authentication.getName()), OK);
    }

    @PostMapping("/quizzes")
    ResponseEntity<Void> newQuiz(@RequestBody QuizCreateDto quizCreateDto, final Authentication authentication) throws AlreadyExistsException {
        quizService.createNewQuiz(quizCreateDto, authentication.getName());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping(value = "/quizzes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> updateQuiz(@PathVariable final int id, @RequestBody QuizEditDto quizEditDto) {
        quizService.editQuiz(id, quizEditDto);
        return new ResponseEntity<>(OK);
    }

    @GetMapping("/quizzes/{id}/edit")
    ResponseEntity<QuizEditDisplayDto> getQuizByIdToDisplayEdit(@PathVariable final int id) {
        return new ResponseEntity<>(quizService.getQuizByIdToDisplayEdit(id), OK);
    }

    @GetMapping("/quizzes/{id}/take")
    ResponseEntity<QuizTakeToSolveDto> getQuizByIdToSolve(@PathVariable final int id) {
        return new ResponseEntity<>(quizService.getQuizByIdToSolve(id), OK);
    }

    @PostMapping("/quizzes/{id}/take")
    ResponseEntity<Void> solveQuiz(@PathVariable final int id, @RequestBody QuizSolveDto quizSolveDto) throws AlreadyExistsException {
        quizService.solveQuiz(id, quizSolveDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @GetMapping("/quizzes/{id}/results")
    ResponseEntity<QuizResultDto> getResults(@PathVariable final int id) {
        return new ResponseEntity<>(quizService.getResults(id), OK);
    }

    @DeleteMapping("/quizzes/{id}")
    ResponseEntity<Void> deleteQuizById(@PathVariable final int id) {
        quizService.deleteQuizById(id);
        return new ResponseEntity<>(OK);
    }

}
