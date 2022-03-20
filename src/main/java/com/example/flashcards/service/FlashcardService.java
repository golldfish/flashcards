package com.example.flashcards.service;

import com.example.flashcards.dto.flashcard.FlashcardDto;
import com.example.flashcards.exception.BadRequestException;
import com.example.flashcards.exception.ConflictException;
import com.example.flashcards.exception.NotFoundException;
import com.example.flashcards.model.Answer;
import com.example.flashcards.model.Flashcard;
import com.example.flashcards.model.Question;
import com.example.flashcards.model.User;
import com.example.flashcards.repository.FlashcardRepository;
import com.example.flashcards.repository.LanguageRepository;
import com.example.flashcards.repository.UserRepository;
import com.example.flashcards.validation.FlashcardValidator;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FlashcardService {

    private final FlashcardRepository flashcardRepository;
    private final UserRepository userRepository;
    private final LanguageRepository languageRepository;
    private final FlashcardValidator flashcardValidator;

    @Transactional(readOnly = true)
    public List<FlashcardDto> getFlashcards(final String username) {
        final User user = userRepository.findUserByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
        final List<Flashcard> flashcards = flashcardRepository.findAllByUser(user);
        return flashcards.stream().map(FlashcardDto::createFrom).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FlashcardDto getFlashcardById(final int id, final String username) {
        userRepository.findUserByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
        final Flashcard flashcard = flashcardRepository.findById(id).orElseThrow(() -> new NotFoundException("Flashcard not found"));
        return FlashcardDto.createFrom(flashcard);
    }

    @Transactional
    public void createFlashcard(final FlashcardDto flashcardDto, final String username) {
        flashcardValidator.validateFlashcardParameters(flashcardDto);

        final User user = userRepository.findUserByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));

        List<Flashcard> flashcards = flashcardRepository.findAllByUser(user);

        flashcards.stream().map(Flashcard::getQuestion)
                .filter(q -> Objects.equals(q.getValue(), flashcardDto.getQuestion().getValue())).findFirst()
                .ifPresent(f -> {
                    throw new ConflictException("Flashcard already exists");
                });

        final Question question = Question.builder().language(
                languageRepository.findByLangCode(flashcardDto.getQuestion().getLangCode())
                        .orElseThrow(() -> new NotFoundException("Language not found"))).value(flashcardDto.getQuestion().getValue()).build();

        final Answer answer = Answer.builder().language(
                languageRepository.findByLangCode(flashcardDto.getAnswer().getLangCode())
                        .orElseThrow(() -> new NotFoundException("Language not found"))).value(flashcardDto.getAnswer().getValue()).build();

        final Flashcard flashcard = Flashcard.builder().question(question).answer(answer).isUsed(false).user(user)
                .creationDate(new Timestamp(System.currentTimeMillis())).build();

        flashcardRepository.save(flashcard);

    }

    @Transactional
    public void editFlashcard(final int id, final FlashcardDto flashcardDto, final String username) {
        flashcardValidator.validateFlashcardParameters(flashcardDto);
        userRepository.findUserByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));

        final Flashcard flashcard = flashcardRepository.findById(id).orElseThrow(()-> new NotFoundException("Flashcard not found"));

        flashcardRepository.findByUserUsernameAndQuestionValue(username, flashcardDto.getQuestion().getValue()).ifPresent(f -> {
            throw new ConflictException("Flashcard with inputted question already exists");
        });

        flashcard.getQuestion().setValue(flashcardDto.getQuestion().getValue());
        flashcard.getAnswer().setValue(flashcardDto.getAnswer().getValue());

        flashcardRepository.save(flashcard);
    }

    @Transactional
    public void deleteFlashcardById(final int id, final String username) {
        userRepository.findUserByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));

        flashcardRepository.findById(id).ifPresentOrElse(flashcard -> {
            if (!flashcard.isUsed()) {
                flashcardRepository.deleteById(id);
            } else {
                throw new BadRequestException("Flashcard is used in quiz - cannot be deleted");
            }
        }, () -> {
            throw new NotFoundException("Flashcard not found");
        });
    }

    @Transactional(readOnly = true)
    public List<FlashcardDto> sortFlashcards(final String questionLangCode, final String answerLangCode, final String username) {
        final User user = userRepository.findUserByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));

        if (StringUtils.isBlank(questionLangCode) && StringUtils.isBlank(answerLangCode)) {
            return flashcardRepository.findAllByUser(user).stream().map(FlashcardDto::createFrom).collect(Collectors.toList());
        } else if (StringUtils.isBlank(questionLangCode) && StringUtils.isNotBlank(answerLangCode)) {
            return flashcardRepository.findAllByUserAndAnswerLanguageLangCode(user, answerLangCode).stream().map(FlashcardDto::createFrom).collect(Collectors.toList());
        } else if (StringUtils.isNotBlank(questionLangCode) && StringUtils.isBlank(answerLangCode)) {
            return flashcardRepository.findAllByUserAndQuestionLanguageLangCode(user, questionLangCode).stream().map(FlashcardDto::createFrom).collect(Collectors.toList());
        } else {
            return flashcardRepository.findAllByUserAndQuestionLanguageLangCodeAndAnswerLanguageLangCode(user, questionLangCode, answerLangCode)
                    .stream().map(FlashcardDto::createFrom).collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public List<FlashcardDto> searchFlashcards(final String questionLangCode, final String answerLangCode, final String questionQuery, final String username) {
        final User user = userRepository.findUserByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));

        if (StringUtils.isBlank(questionLangCode) && StringUtils.isBlank(answerLangCode)) {
            return flashcardRepository.findAllByUserAndQuestionContaining(user, questionQuery).stream().map(FlashcardDto::createFrom).collect(Collectors.toList());
        } else if (StringUtils.isBlank(questionLangCode) && StringUtils.isNotBlank(answerLangCode)) {
            return flashcardRepository.findAllByUserAndAnswerLangCodeAndQuestionContaining(user, questionQuery, answerLangCode).stream().map(FlashcardDto::createFrom).collect(Collectors.toList());
        } else if (StringUtils.isNotBlank(questionLangCode) && StringUtils.isBlank(answerLangCode)) {
            return flashcardRepository.findAllByUserAndQuestionLangCodeAndQuestionContaining(user, questionQuery, questionLangCode).stream().map(FlashcardDto::createFrom).collect(Collectors.toList());
        } else {
            return flashcardRepository.findAllByUserAndQuestionLangCodeAndAnswerLangCodeAndQuestionContaining(user, questionQuery, questionLangCode, answerLangCode)
                    .stream().map(FlashcardDto::createFrom).collect(Collectors.toList());
        }
    }
}
