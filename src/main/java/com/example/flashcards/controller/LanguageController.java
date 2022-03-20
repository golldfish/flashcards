package com.example.flashcards.controller;

import com.example.flashcards.dto.flashcard.LanguageDto;
import com.example.flashcards.service.LanguageService;
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

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class LanguageController {

    private final LanguageService service;

    @GetMapping("/languages")
    @ResponseStatus(OK)
    @Operation(security = @SecurityRequirement(name = "token"), summary = "Get all languages",
            responses = {@ApiResponse(responseCode = "200", description = "Data collected"),
                    @ApiResponse(responseCode = "404", description = "User not found")})
    List<LanguageDto> all(final Authentication authentication) {

        return service.getLanguages(authentication.getName());
    }

    @GetMapping("/languages/{langCode}")
    @ResponseStatus(OK)
    @Operation(security = @SecurityRequirement(name = "token"), summary = "Get language by lang code",
            responses = {@ApiResponse(responseCode = "200", description = "Data collected"),
                    @ApiResponse(responseCode = "404", description = "Language/User not found")})
    LanguageDto getLanguageByLangCode(@PathVariable final String langCode, final Authentication authentication) {

        return service.getLanguageByLangCode(langCode, authentication.getName());
    }

    @PostMapping("/admin/languages")
    @ResponseStatus(CREATED)
    @Operation(security = @SecurityRequirement(name = "token"), summary = "Create language",
            responses = {@ApiResponse(responseCode = "201", description = "Created"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "409", description = "Language already exists")})
    void add(@RequestBody final LanguageDto languageDto, final Authentication authentication) {

        service.createLanguage(languageDto, authentication.getName());
    }

    @PutMapping(value = "/admin/languages/{langCode}")
    @ResponseStatus(OK)
    @Operation(security = @SecurityRequirement(name = "token"), summary = "Edit language data",
            responses = {@ApiResponse(responseCode = "200", description = "Edited"),
                    @ApiResponse(responseCode = "404", description = "Language/User not found")})
    void editLanguage(@PathVariable final String langCode, @RequestBody final LanguageDto languageDto, final Authentication authentication) {

        service.editLanguageData(langCode, languageDto, authentication.getName());
    }

    @DeleteMapping(value = "/admin/languages/{langCode}")
    @ResponseStatus(OK)
    @Operation(security = @SecurityRequirement(name = "token"), summary = "Delete language",
            responses = {@ApiResponse(responseCode = "200", description = "Data collected"),
                    @ApiResponse(responseCode = "400", description = "Language cannot be deleted - it's used in flashcards"),
                    @ApiResponse(responseCode = "404", description = "Language/User not found")})
    void delete(@PathVariable final String langCode, final Authentication authentication) {

        service.deleteLanguage(langCode, authentication.getName());
    }
}
