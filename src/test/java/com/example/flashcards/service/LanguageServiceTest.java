package com.example.flashcards.service;

import com.example.flashcards.dto.flashcard.LanguageDto;
import com.example.flashcards.exception.NotFoundException;
import com.example.flashcards.model.Language;
import com.example.flashcards.model.User;
import com.example.flashcards.repository.FlashcardRepository;
import com.example.flashcards.repository.LanguageRepository;
import com.example.flashcards.repository.UserRepository;
import com.example.flashcards.validation.LanguageValidator;
import jdk.jfr.Description;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

}
