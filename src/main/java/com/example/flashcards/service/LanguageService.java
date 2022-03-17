package com.example.flashcards.service;

import com.example.flashcards.dto.flashcard.LanguageDto;
import com.example.flashcards.exception.BadRequestException;
import com.example.flashcards.exception.ConflictException;
import com.example.flashcards.exception.NotFoundException;
import com.example.flashcards.model.Flashcard;
import com.example.flashcards.model.Language;
import com.example.flashcards.repository.FlashcardRepository;
import com.example.flashcards.repository.LanguageRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LanguageService {

    private final LanguageRepository languageRepository;
    private final FlashcardRepository flashcardRepository;

    @Transactional(readOnly = true)
    public List<LanguageDto> getLanguages() {
        final List<Language> languages = languageRepository.findAll();
        return languages.stream().map(LanguageDto::createFrom).collect(Collectors.toList());
    }

    public LanguageDto getLanguageByLangCode(final String langCode) {
        final Language lang = languageRepository.findByLangCode(langCode).orElseThrow(NotFoundException::new);
        return LanguageDto.createFrom(lang);
    }

    public void createNewLanguage(final LanguageDto languageDto) {
        if (StringUtils.isBlank(languageDto.getLangCode()) || StringUtils.isBlank(languageDto.getName())) {
            throw new ConflictException();
        }

        languageRepository.findAll().stream().map(Language::getName).filter(l -> l.equals(languageDto.getLangCode()))
                .findFirst().ifPresentOrElse(lang -> {
                    throw new ConflictException();
                }, () -> {
                    final Language language =
                            Language.builder().langCode(languageDto.getLangCode()).name(languageDto.getName()).build();
                    languageRepository.save(language);
                });
    }

    public void changeLanguageData(final String langCode, final LanguageDto languageDto) {
        final Language lang = languageRepository.findByLangCode(langCode).orElseThrow(NotFoundException::new);
        if (StringUtils.isBlank(languageDto.getName())) {
            throw new ConflictException();
        }
        lang.setName(languageDto.getName());
        languageRepository.save(lang);
    }

    public void deleteLanguage(final String langCode) {
        final List<Flashcard> questionsWithLangCode = flashcardRepository.findAllByQuestionLanguageLangCode(langCode);
        final List<Flashcard> answersWithLangCode = flashcardRepository.findAllByAnswerLanguageLangCode(langCode);

        if (! answersWithLangCode.isEmpty() || ! questionsWithLangCode.isEmpty()) {
            throw new BadRequestException();
        } else {
            languageRepository.deleteByLangCode(langCode);
        }
    }
}
