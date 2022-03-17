package com.example.flashcards.controller;

import com.example.flashcards.dto.flashcard.FlashcardDto;
import com.example.flashcards.dto.flashcard.FlashcardForQuizDto;
import com.example.flashcards.service.FlashcardService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    List<FlashcardDto> all(final Authentication authentication) {
        return flashcardsService.getFlashcards(authentication.getName());
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    FlashcardDto getFlashcardById(@PathVariable final int id) {

        return flashcardsService.getFlashcardById(id);
    }

    @PostMapping()
    @ResponseStatus(CREATED)
    void createFlashcard(@RequestBody final FlashcardDto flashcardDto, final Authentication authentication) {
        flashcardsService.createFlashcard(flashcardDto, authentication.getName());
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    void changeFlashcardData(@PathVariable final int id, @RequestBody final FlashcardDto flashcardDto) {
        flashcardsService.changeFlashcardData(id, flashcardDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(OK)
    void deleteFlashcardById(@PathVariable final int id) {
        flashcardsService.deleteFlashcardById(id);
    }

    @RequestMapping(params = {"questionLangCode", "answerLangCode"})
    @GetMapping()
    @ResponseStatus(OK)
    List<FlashcardForQuizDto> getFlashcardsByLangCodes(@RequestParam("questionLangCode") final String questionLangCode,
                                                       @RequestParam("answerLangCode") final String answerLangCode,
                                                       final Authentication authentication) {
        return flashcardsService.getFlashcardsByLangCodes(questionLangCode, answerLangCode, authentication.getName());
    }

    @RequestMapping(params = {"questionLangCode"})
    @GetMapping()
    @ResponseStatus(OK)
    List<FlashcardDto> getFlashcardsByQuestionLangCode(@RequestParam("questionLangCode") final String questionLangCode,
                                                       final Authentication authentication) {
        return flashcardsService.getFlashcardsByQuestionLangCode(questionLangCode, authentication.getName());
    }

    @RequestMapping(params = {"answerLangCode"})
    @GetMapping()
    @ResponseStatus(OK)
    List<FlashcardDto> getFlashcardsByAnswerLangCode(@RequestParam("answerLangCode") final String answerLangCode,
                                                     final Authentication authentication) {
        return flashcardsService.getFlashcardsByAnswerLangCode(answerLangCode, authentication.getName());
    }

    @RequestMapping(params = {"questionQuery"})
    @GetMapping()
    @ResponseStatus(OK)
    List<FlashcardDto> getFlashcardsByQuestionQuery(@RequestParam("questionQuery") final String questionQuery,
                                                    final Authentication authentication) {
        return flashcardsService.getFlashcardsByQuestionQuery(questionQuery, authentication.getName());
    }

    @RequestMapping(params = {"questionLangCode", "answerLangCode", "questionQuery"})
    @GetMapping()
    @ResponseStatus(OK)
    List<FlashcardDto> getFlashcardsByQuestionLangCodeAndAnswerLangCodeAndQuestionQuery(
            @RequestParam("questionLangCode") final String questionLangCode,
            @RequestParam("answerLangCode") final String answerLangCode,
            @RequestParam("questionQuery") final String questionQuery, final Authentication authentication) {
        return flashcardsService.getFlashcardsByQuestionLangCodeAndAnswerLangCodeAndQuestionQuery(questionLangCode,
                answerLangCode, questionQuery, authentication.getName());
    }

    @RequestMapping(params = {"questionLangCode", "questionQuery"})
    @GetMapping()
    @ResponseStatus(OK)
    List<FlashcardDto> getFlashcardsByQuestionLangCodeAndQuestionQuery(
            @RequestParam("questionLangCode") final String questionLangCode,
            @RequestParam("questionQuery") final String questionQuery, final Authentication authentication) {
        return flashcardsService.getFlashcardsByQuestionLangCodeAndQuestionQuery(questionLangCode, questionQuery,
                authentication.getName());
    }

    @RequestMapping(params = {"answerLangCode", "questionQuery"})
    @GetMapping()
    @ResponseStatus(OK)
    List<FlashcardDto> getFlashcardsByAnswerLangCodeAndQuestionQuery(
            @RequestParam("answerLangCode") final String answerLangCode,
            @RequestParam("questionQuery") final String questionQuery, final Authentication authentication) {
        return flashcardsService.getFlashcardsByAnswerLangCodeAndQuestionQuery(answerLangCode, questionQuery,
                authentication.getName());
    }
}
