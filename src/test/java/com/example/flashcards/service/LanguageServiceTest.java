package com.example.flashcards.service;

import com.example.flashcards.dto.flashcard.LanguageDto;
import com.example.flashcards.exception.BadRequestException;
import com.example.flashcards.exception.ConflictException;
import com.example.flashcards.exception.NotFoundException;
import com.example.flashcards.model.*;
import com.example.flashcards.repository.FlashcardRepository;
import com.example.flashcards.repository.LanguageRepository;
import com.example.flashcards.repository.UserRepository;
import com.example.flashcards.validation.LanguageValidator;
import jdk.jfr.Description;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LanguageService.class)
class LanguageServiceTest {

    @Autowired
    private LanguageService languageService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private LanguageRepository languageRepository;

    @MockBean
    private FlashcardRepository flashcardRepository;

    @MockBean
    private LanguageValidator languageValidator;

    private static final String USER_NOT_FOUND_EXCEPTION_MESSAGE = "User not found";
    private static final String LANGUAGE_NOT_FOUND_EXCEPTION_MESSAGE = "Language not found";

    @Test
    @Description("getLanguages should return all languages data")
    void getLanguagesShouldReturnListOfLanguagesDto() {
        //given
        final String username = "username";
        final User user = buildUser(username);
        final Language polish = buildLanguage("POL", "Polish");
        final Language english = buildLanguage("ENG", "English");

        //when
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
        when(languageRepository.findAll()).thenReturn(List.of(polish, english));
        final List<LanguageDto> languageDtos = languageService.getLanguages(username);

        //then
        assertEquals(2, languageDtos.size());
        assertEquals(polish.getLangCode(), languageDtos.get(0).getLangCode());
        assertEquals(polish.getName(), languageDtos.get(0).getName());
        assertEquals(english.getLangCode(), languageDtos.get(1).getLangCode());
        assertEquals(english.getName(), languageDtos.get(1).getName());
    }


    @Test
    @Description("getLanguages should throw 404 when user is not found")
    void getLanguagesShouldThrowNotFoundWhenUserDoesNotExist() {
        //given
        final String username = "username";

        //when
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());

        //then
        final NotFoundException exception =
                assertThrows(NotFoundException.class, () -> languageService.getLanguages(username));
        assertEquals(USER_NOT_FOUND_EXCEPTION_MESSAGE, exception.getMessage());
    }

    @Test
    @Description("getLanguageByLangCode should return language data")
    void getLanguageByLangCodeShouldReturnLanguageDto() {
        //given
        final String langCode = "POL";
        final String username = "username";
        final User user = buildUser(username);
        final Language polish = buildLanguage(langCode, "Polish");

        //when
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
        when(languageRepository.findByLangCode(langCode)).thenReturn(Optional.of(polish));
        final LanguageDto languageDto = languageService.getLanguageByLangCode(langCode, username);

        //then
        assertEquals(polish.getLangCode(), languageDto.getLangCode());
        assertEquals(polish.getName(), languageDto.getName());
    }


    @Test
    @Description("getLanguageByLangCode should throw 404 when user is not found")
    void getLanguageByLangCodeShouldThrowNotFoundWhenUserDoesNotExist() {
        //given
        final String langCode = "POL";
        final String username = "username";

        //when
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());

        //then
        final NotFoundException exception =
                assertThrows(NotFoundException.class, () -> languageService.getLanguageByLangCode(langCode, username));
        assertEquals(USER_NOT_FOUND_EXCEPTION_MESSAGE, exception.getMessage());
    }

    @Test
    @Description("getLanguageByLangCode should throw 404 when language is not found")
    void getLanguageByLangCodeShouldThrowNotFoundWhenLanguageDoesNotExist() {
        //given
        final String langCode = "POL";
        final String username = "username";
        final User user = buildUser(username);

        //when
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
        when(languageRepository.findByLangCode(langCode)).thenReturn(Optional.empty());

        //then
        final NotFoundException exception =
                assertThrows(NotFoundException.class, () -> languageService.getLanguageByLangCode(langCode, username));
        assertEquals(LANGUAGE_NOT_FOUND_EXCEPTION_MESSAGE, exception.getMessage());
    }

    @Test
    @Description("createLanguage should throw 404 when user is not found")
    void createLanguageShouldThrowNotFoundWhenUserDoesNotExist() {
        //given
        final String langCode = "POL";
        final String username = "username";
        final LanguageDto languageDto = LanguageDto.builder()
                .langCode(langCode)
                .name("polski")
                .build();

        //when
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());

        //then
        final NotFoundException exception =
                assertThrows(NotFoundException.class, () -> languageService.createLanguage(languageDto, username));
        assertEquals(USER_NOT_FOUND_EXCEPTION_MESSAGE, exception.getMessage());
    }

    @Test
    @Description("createLanguage should throw 409 when language already exists")
    void createLanguageShouldThrowConflictWhenLanguageAlreadyExist() {
        //given
        final String langCode = "POL";
        final String username = "username";
        final String exceptionMessage = "Language already exists";
        final User user = buildUser(username);
        final Language language = buildLanguage(langCode, "polski");
        final LanguageDto languageDto = LanguageDto.builder()
                .langCode(langCode)
                .name("polski")
                .build();

        //when
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
        when(languageRepository.findAll()).thenReturn(List.of(language));

        //then
        final ConflictException exception = assertThrows(ConflictException.class, () -> languageService.createLanguage(languageDto, username));
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    @Description("createLanguage should save language to repository")
    void createLanguageShouldSaveLanguage() {
        //given
        final String langCode = "POL";
        final String username = "username";
        final User user = buildUser(username);
        final LanguageDto languageDto = LanguageDto.builder()
                .langCode(langCode)
                .name("polski")
                .build();

        //when
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
        when(languageRepository.findAll()).thenReturn(List.of());
        languageService.createLanguage(languageDto, username);

        //then
        verify(languageRepository).save(any(Language.class));
    }

    @Test
    @Description("editLanguageData should throw 404 when user is not found")
    void editLanguageDataShouldThrowNotFoundWhenUserDoesNotExist() {
        //given
        final String langCode = "POL";
        final String username = "username";
        final LanguageDto languageDto = LanguageDto.builder()
                .name("pl")
                .build();

        //when
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());

        //then
        final NotFoundException exception =
                assertThrows(NotFoundException.class, () -> languageService.editLanguageData(langCode, languageDto, username));
        assertEquals(USER_NOT_FOUND_EXCEPTION_MESSAGE, exception.getMessage());
    }

    @Test
    @Description("editLanguageData should throw 404 when language is not found")
    void editLanguageDataShouldThrowNotFoundWhenLanguageDoesNotExist() {
        //given
        final String langCode = "POL";
        final String username = "username";
        final User user = buildUser(username);
        final LanguageDto languageDto = LanguageDto.builder()
                .name("pl")
                .build();

        //when
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
        when(languageRepository.findByLangCode(langCode)).thenReturn(Optional.empty());


        //then
        final NotFoundException exception =
                assertThrows(NotFoundException.class, () -> languageService.editLanguageData(langCode, languageDto, username));
        assertEquals(LANGUAGE_NOT_FOUND_EXCEPTION_MESSAGE, exception.getMessage());
    }

    @Test
    @Description("editLanguageData should save new language data")
    void editLanguageDataShouldSaveEditedData() {
        //given
        final String langCode = "POL";
        final String newLanguageName = "pl";
        final String username = "username";
        final User user = buildUser(username);
        final Language language = buildLanguage(langCode, "polski");
        final LanguageDto languageDto = LanguageDto.builder()
                .name(newLanguageName)
                .build();

        //when
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
        when(languageRepository.findByLangCode(langCode)).thenReturn(Optional.of(language));
        languageService.editLanguageData(langCode, languageDto, username);

        //then
        final ArgumentCaptor<Language> languageCaptor = ArgumentCaptor.forClass(Language.class);
        verify(languageRepository).save(languageCaptor.capture());
        final Language savedLanguage = languageCaptor.getValue();

        assertEquals(newLanguageName, savedLanguage.getName());
        verify(languageRepository).save(any(Language.class));

    }

    @Test
    @Description("deleteLanguage should throw 404 when user is not found")
    void deleteLanguageShouldThrowNotFoundWhenUserDoesNotExist() {
        //given
        final String langCode = "POL";
        final String username = "username";

        //when
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());

        //then
        final NotFoundException exception =
                assertThrows(NotFoundException.class, () -> languageService.deleteLanguage(langCode, username));
        assertEquals(USER_NOT_FOUND_EXCEPTION_MESSAGE, exception.getMessage());
    }

    @Test
    @Description("deleteLanguage should throw 404 when language is not found")
    void deleteLanguageShouldThrowNotFoundWhenLanguageDoesNotExist() {
        //given
        final String langCode = "POL";
        final String username = "username";
        final User user = buildUser(username);

        //when
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
        when(languageRepository.findByLangCode(langCode)).thenReturn(Optional.empty());

        //then
        final NotFoundException exception =
                assertThrows(NotFoundException.class, () -> languageService.deleteLanguage(langCode, username));
        assertEquals(LANGUAGE_NOT_FOUND_EXCEPTION_MESSAGE, exception.getMessage());
    }

    @Test
    @Description("deleteLanguage should throw 400 when language is used in questions")
    void deleteLanguageShouldThrowBadRequestWhenLanguageIsUsedInQuestions() {
        //given
        final String langCode = "POL";
        final String username = "username";
        final String exceptionMessage = "Could not remove language";
        final User user = buildUser(username);
        final Language language = buildLanguage(langCode, "polski");
        final List<Flashcard> questionFlashcards = List.of(buildFlashcard(language, user));

        //when
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
        when(languageRepository.findByLangCode(langCode)).thenReturn(Optional.of(language));
        when(flashcardRepository.findAllByQuestionLanguageLangCode(langCode)).thenReturn(questionFlashcards);
        when(flashcardRepository.findAllByAnswerLanguageLangCode(langCode)).thenReturn(List.of());

        //then
        final BadRequestException exception =
                assertThrows(BadRequestException.class, () -> languageService.deleteLanguage(langCode, username));
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    @Description("deleteLanguage should throw 400 when language is used in answers")
    void deleteLanguageShouldThrowBadRequestWhenLanguageIsUsedInAnswers() {
        //given
        final String langCode = "POL";
        final String username = "username";
        final String exceptionMessage = "Could not remove language";
        final User user = buildUser(username);
        final Language language = buildLanguage(langCode, "polski");
        final List<Flashcard> answerFlashcards = List.of(buildFlashcard(language, user));

        //when
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
        when(languageRepository.findByLangCode(langCode)).thenReturn(Optional.of(language));
        when(flashcardRepository.findAllByQuestionLanguageLangCode(langCode)).thenReturn(List.of());
        when(flashcardRepository.findAllByAnswerLanguageLangCode(langCode)).thenReturn(answerFlashcards);

        //then
        final BadRequestException exception =
                assertThrows(BadRequestException.class, () -> languageService.deleteLanguage(langCode, username));
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    @Description("deleteLanguage should remove language")
    void deleteLanguageShouldRemoveLanguage() {
        //given
        final String langCode = "POL";
        final String username = "username";
        final String exceptionMessage = "Could not remove language";
        final User user = buildUser(username);
        final Language language = buildLanguage(langCode, "polski");

        //when
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));
        when(languageRepository.findByLangCode(langCode)).thenReturn(Optional.of(language));
        when(flashcardRepository.findAllByQuestionLanguageLangCode(langCode)).thenReturn(List.of());
        when(flashcardRepository.findAllByAnswerLanguageLangCode(langCode)).thenReturn(List.of());
        languageService.deleteLanguage(langCode, username);

        //then
        verify(languageRepository).deleteByLangCode(langCode);
    }

    private User buildUser(final String username) {
        return User.builder()
                .username(username)
                .email("test@example.com")
                .role("user")
                .flashcards(Set.of())
                .quizzes(Set.of())
                .build();
    }

    private Language buildLanguage(final String langCode, final String languageName) {
        return Language.builder()
                .langCode(langCode)
                .name(languageName)
                .answer(Set.of())
                .questions(Set.of())
                .build();
    }

    private Flashcard buildFlashcard(final Language language, final User user) {
        return Flashcard.builder()
                .id(1)
                .isUsed(false)
                .creationDate(new Date())
                .question(buildQuestion(language))
                .answer(buildAnswer(language))
                .user(user)
                .quizFlashcards(Set.of())
                .build();
    }

    private Question buildQuestion(final Language language) {
        return Question.builder()
                .id(1)
                .language(language)
                .value("question value")
                .build();
    }

    private Answer buildAnswer(final Language language) {
        return Answer.builder()
                .id(1)
                .language(language)
                .value("answer value")
                .build();
    }
}
