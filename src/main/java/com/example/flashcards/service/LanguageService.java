package com.example.flashcards.service;

import com.example.flashcards.dto.flashcard.LanguageDto;
import com.example.flashcards.exception.BadRequestException;
import com.example.flashcards.exception.ConflictException;
import com.example.flashcards.exception.NotFoundException;
import com.example.flashcards.model.Flashcard;
import com.example.flashcards.model.Language;
import com.example.flashcards.repository.FlashcardRepository;
import com.example.flashcards.repository.LanguageRepository;
import com.example.flashcards.repository.UserRepository;
import com.example.flashcards.validation.LanguageValidator;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LanguageService {

    private final UserRepository userRepository;
    private final LanguageRepository languageRepository;
    private final FlashcardRepository flashcardRepository;
    private final LanguageValidator languageValidator;

    @Transactional(readOnly = true)
    public List<LanguageDto> getLanguages(final String username) {
        userRepository.findUserByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));

        return languageRepository.findAll().stream().map(LanguageDto::createFrom).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LanguageDto getLanguageByLangCode(final String langCode, final String username) {
        userRepository.findUserByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));

        final Language lang = languageRepository.findByLangCode(langCode).orElseThrow(() -> new NotFoundException("Language not found"));
        return LanguageDto.createFrom(lang);
    }

    @Transactional
    public void createLanguage(final LanguageDto languageDto, final String username) {
        languageValidator.validateLanguageParameters(languageDto, false);

        userRepository.findUserByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));

        languageRepository.findAll().stream().map(Language::getLangCode).filter(l -> l.equals(languageDto.getLangCode()))
                .findFirst().ifPresentOrElse(lang -> {
                    throw new ConflictException("Language already exists");
                }, () -> {
                    final Language language =
                            Language.builder().langCode(languageDto.getLangCode()).name(languageDto.getName()).build();
                    languageRepository.save(language);
                });
    }

    @Transactional
    public void editLanguageData(final String langCode, final LanguageDto languageDto, final String username) {
        userRepository.findUserByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));

        final Language lang = languageRepository.findByLangCode(langCode).orElseThrow(() -> new NotFoundException("Language not found"));

        languageValidator.validateLanguageParameters(languageDto, true);

        lang.setName(languageDto.getName());
        languageRepository.save(lang);
    }

    @Transactional
    public void deleteLanguage(final String langCode, final String username) {
        userRepository.findUserByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
        languageRepository.findByLangCode(langCode).orElseThrow(()-> new NotFoundException("Language not found"));

        final List<Flashcard> questionsWithLangCode = flashcardRepository.findAllByQuestionLanguageLangCode(langCode);
        final List<Flashcard> answersWithLangCode = flashcardRepository.findAllByAnswerLanguageLangCode(langCode);

        if (!answersWithLangCode.isEmpty() || !questionsWithLangCode.isEmpty()) {
            throw new BadRequestException("Could not remove language");
        } else {
            languageRepository.deleteByLangCode(langCode);
        }
    }
}
