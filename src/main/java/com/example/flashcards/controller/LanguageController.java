package com.example.flashcards.controller;

import com.example.flashcards.dto.flashcard.LanguageDto;
import com.example.flashcards.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class LanguageController {

    private final LanguageService service;

    @GetMapping("/languages")
    @ResponseStatus(OK)
    List<LanguageDto> all() {
        return service.getLanguages();
    }

    @GetMapping("/languages/{langCode}")
    @ResponseStatus(OK)
    LanguageDto getLanguageByLangCode(@PathVariable final String langCode) {
        return service.getLanguageByLangCode(langCode);
    }

    @PostMapping("/admin/languages")
    @ResponseStatus(CREATED)
    void add(@RequestBody LanguageDto languageDto) {
        service.createNewLanguage(languageDto);
    }

    @PutMapping(value = "/admin/languages/{langCode}")
    @ResponseStatus(OK)
    void update(@PathVariable final String langCode, @RequestBody LanguageDto languageDto) {
        service.changeLanguageData(langCode, languageDto);
    }

    @DeleteMapping(value = "/admin/languages/{langCode}")
    @ResponseStatus(OK)
    void delete(@PathVariable final String langCode) {
        service.deleteLanguage(langCode);
    }
}
