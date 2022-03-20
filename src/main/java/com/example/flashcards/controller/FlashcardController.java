package com.example.flashcards.controller;

import com.example.flashcards.dto.flashcard.FlashcardDto;
import com.example.flashcards.service.FlashcardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/flashcards")
public class FlashcardController {

    private FlashcardService flashcardsService;

    @GetMapping()
    @ResponseStatus(OK)
    @Operation(security = @SecurityRequirement(name = "token"), summary = "Get flashcards for user",
            responses = {@ApiResponse(responseCode = "200", description = "Data collected"),
                    @ApiResponse(responseCode = "404", description = "User not found")})
    List<FlashcardDto> all(final Authentication authentication) {
        return flashcardsService.getFlashcards(authentication.getName());
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    @Operation(security = @SecurityRequirement(name = "token"), summary = "Get flashcard by id",
            responses = {@ApiResponse(responseCode = "200", description = "Data collected"),
                    @ApiResponse(responseCode = "404", description = "Flashcard/User not found")})
    FlashcardDto getFlashcardById(@PathVariable final int id, final Authentication authentication) {

        return flashcardsService.getFlashcardById(id, authentication.getName());
    }

    @PostMapping()
    @ResponseStatus(CREATED)
    @Operation(security = @SecurityRequirement(name = "token"), summary = "Create new flashcard",
            responses = {@ApiResponse(responseCode = "201", description = "Created"),
                    @ApiResponse(responseCode = "400", description = "Invalid data"),
                    @ApiResponse(responseCode = "404", description = "Language/User not found"),
                    @ApiResponse(responseCode = "409", description = "Flashcard with inputted question already exists")})
    void createFlashcard(@RequestBody final FlashcardDto flashcardDto, final Authentication authentication) {

        flashcardsService.createFlashcard(flashcardDto, authentication.getName());
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    @Operation(security = @SecurityRequirement(name = "token"), summary = "Edit flashcard",
            responses = {@ApiResponse(responseCode = "200", description = "Changed"),
                    @ApiResponse(responseCode = "400", description = "Invalid data"),
                    @ApiResponse(responseCode = "404", description = "Flashcard/User not found"),
                    @ApiResponse(responseCode = "409", description = "Flashcard with inputted question already exists")})
    void editFlashcard(@PathVariable final int id, @RequestBody final FlashcardDto flashcardDto, final Authentication authentication) {

        flashcardsService.editFlashcard(id, flashcardDto, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(OK)
    @Operation(security = @SecurityRequirement(name = "token"), summary = "Delete flashcard",
            responses = {@ApiResponse(responseCode = "200", description = "Deleted"),
                    @ApiResponse(responseCode = "400", description = "Invalid data"),
                    @ApiResponse(responseCode = "404", description = "Flashcard/User not found"),
                    @ApiResponse(responseCode = "409", description = "Flashcard with inputted question already exists")})
    void deleteFlashcardById(@PathVariable final int id, final Authentication authentication) {

        flashcardsService.deleteFlashcardById(id, authentication.getName());
    }

    @GetMapping("/sort")
    @ResponseStatus(OK)
    @Operation(security = @SecurityRequirement(name = "token"), summary = "Sort flashcard",
            responses = {@ApiResponse(responseCode = "200", description = "Sorted"),
                    @ApiResponse(responseCode = "404", description = "User not found")})
    List<FlashcardDto> sortFlashcards(@RequestParam(value = "questionLangCode", required = false) final String questionLangCode,
                                      @RequestParam(value = "answerLangCode", required = false) final String answerLangCode,
                                      final Authentication authentication) {

        return flashcardsService.sortFlashcards(questionLangCode, answerLangCode, authentication.getName());
    }

    @GetMapping("/search")
    @ResponseStatus(OK)
    @Operation(security = @SecurityRequirement(name = "token"), summary = "Sort flashcard",
            responses = {@ApiResponse(responseCode = "200", description = "Sorted"),
                    @ApiResponse(responseCode = "404", description = "User not found")})
    List<FlashcardDto> searchFlashcards(
            @RequestParam(value = "questionLangCode", required = false) final String questionLangCode,
            @RequestParam(value = "answerLangCode", required = false) final String answerLangCode,
            @RequestParam("questionQuery") final String questionQuery, final Authentication authentication) {

        return flashcardsService.searchFlashcards(questionLangCode, answerLangCode, questionQuery, authentication.getName());
    }
}
