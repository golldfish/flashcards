package com.example.flashcards.controller;

import com.example.flashcards.dto.quiz.*;
import com.example.flashcards.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @Operation(security = @SecurityRequirement(name = "token"), summary = "Get all quizzes for user",
            responses = {@ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "User not found")})
    List<QuizDto> getAll(final Authentication authentication) {
        return quizService.getAll(authentication.getName());
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(security = @SecurityRequirement(name = "token"), summary = "Create quiz",
            responses = {@ApiResponse(responseCode = "201", description = "Created"),
                    @ApiResponse(responseCode = "400", description = "Invalid data"),
                    @ApiResponse(responseCode = "404", description = "Flashcard/User not found")})
    QuizDto newQuiz(@RequestBody final QuizCreateDto quizCreateDto, final Authentication authentication) {

        return quizService.createQuiz(quizCreateDto, authentication.getName());
    }

    @PutMapping(value = "/{id}/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    @Operation(security = @SecurityRequirement(name = "token"), summary = "Edit quiz",
            responses = {@ApiResponse(responseCode = "200", description = "Changed"),
                    @ApiResponse(responseCode = "400", description = "Invalid data"),
                    @ApiResponse(responseCode = "404", description = "Quiz/Flashcard/User not found")})
    void editQuiz(@PathVariable final int id, @RequestBody final QuizEditDto quizEditDto, final Authentication authentication) {

        quizService.editQuiz(id, quizEditDto, authentication.getName());
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    @Operation(security = @SecurityRequirement(name = "token"), summary = "Get quiz by id",
            responses = {@ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Quiz/User not found")})
    QuizDetailsDto getQuizDetailsToDisplayEdit(@PathVariable final int id, final Authentication authentication) {

        return quizService.getQuizDetails(id, authentication.getName());
    }

    @GetMapping("/{id}/solve")
    @ResponseStatus(OK)
    @Operation(security = @SecurityRequirement(name = "token"), summary = "Get quiz data to solve quiz",
            responses = {@ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "400", description = "Invalid data"),
                    @ApiResponse(responseCode = "404", description = "Quiz/Flashcard/User not found"),
                    @ApiResponse(responseCode = "409", description = "Flashcard with inputted question already exists")})
    QuizDetailsDto getQuizDetailsToSolveQuiz(@PathVariable final int id, final Authentication authentication) {

        return quizService.getQuizDetails(id, authentication.getName());
    }

    @PostMapping("/{id}/solve")
    @ResponseStatus(CREATED)
    @Operation(security = @SecurityRequirement(name = "token"), summary = "Solve quiz",
            responses = {@ApiResponse(responseCode = "201", description = "Solved"),
                    @ApiResponse(responseCode = "400", description = "Invalid data"),
                    @ApiResponse(responseCode = "404", description = "Quiz/Flashcard/User not found")})
    void solveQuiz(@PathVariable final int id, @RequestBody final List<QuizSolveDto> quizSolveDtos, final Authentication authentication) {

        quizService.solveQuiz(id, quizSolveDtos, authentication.getName());
    }

    @GetMapping("/{id}/results")
    @ResponseStatus(OK)
    @Operation(security = @SecurityRequirement(name = "token"), summary = "Get quiz result",
            responses = {@ApiResponse(responseCode = "200", description = "OK"),
                    @ApiResponse(responseCode = "404", description = "Quiz/User not found")})
    QuizResultDto getResults(@PathVariable final int id, final Authentication authentication) {

        return quizService.getResults(id, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(OK)
    @Operation(security = @SecurityRequirement(name = "token"), summary = "Delete quiz",
            responses = {@ApiResponse(responseCode = "200", description = "Deleted"),
                    @ApiResponse(responseCode = "404", description = "Quiz/User not found")})
    void deleteQuizById(@PathVariable final int id, final Authentication authentication) {

        quizService.deleteQuizById(id, authentication.getName());
    }
}
