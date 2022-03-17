package com.example.flashcards.service;

import com.example.flashcards.dto.flashcard.FlashcardDto;
import com.example.flashcards.dto.flashcard.FlashcardForQuizDto;
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
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class FlashcardService {

    private final FlashcardRepository flashcardRepository;
    private final UserRepository userRepository;
    private final LanguageRepository languageRepository;

    public List<FlashcardDto> getFlashcards(final String username) {
        final User user = userRepository.findUserByUsername(username).orElseThrow(NotFoundException::new);
        final List<Flashcard> flashcards = flashcardRepository.findAllByUser(user);
        return flashcards.stream().map(FlashcardDto::createFrom).collect(Collectors.toList());
    }

    public FlashcardDto getFlashcardById(final int id) {
        final Flashcard flashcard = flashcardRepository.findById(id).orElseThrow(NotFoundException::new);
        return FlashcardDto.createFrom(flashcard);
    }

    public void createFlashcard(final FlashcardDto flashcardDto, final String username) {
        if (StringUtils.isBlank(flashcardDto.getQuestion().getValue()) ||
            StringUtils.isBlank(flashcardDto.getQuestion().getLangCode()) ||
            StringUtils.isBlank(flashcardDto.getAnswer().getValue()) ||
            StringUtils.isBlank(flashcardDto.getAnswer().getLangCode())) {
            throw new BadRequestException();
        }
        final User user = userRepository.findUserByUsername(username).orElseThrow(NotFoundException::new);

        List<Flashcard> flashcards = flashcardRepository.findAllByUser(user);

        flashcards.stream().map(Flashcard::getQuestion)
                .filter(q -> Objects.equals(q.getValue(), flashcardDto.getQuestion().getValue())).findFirst()
                .ifPresent(f -> {
                    throw new ConflictException();
                });

        final Question question = Question.builder().language(
                languageRepository.findByLangCode(flashcardDto.getQuestion().getLangCode())
                        .orElseThrow(NotFoundException::new)).value(flashcardDto.getQuestion().getValue()).build();

        final Answer answer = Answer.builder().language(
                languageRepository.findByLangCode(flashcardDto.getAnswer().getLangCode())
                        .orElseThrow(NotFoundException::new)).value(flashcardDto.getAnswer().getValue()).build();

        final Flashcard flashcard = Flashcard.builder().question(question).answer(answer).isUsed(false).user(user)
                .creationDate(new Timestamp(System.currentTimeMillis())).build();

        flashcardRepository.save(flashcard);

    }

    public void changeFlashcardData(final int id, final FlashcardDto flashcardDto) {
        final Flashcard flashcard = flashcardRepository.findById(id).orElseThrow(NotFoundException::new);
        if (StringUtils.isBlank(flashcardDto.getQuestion().getValue()) ||
            StringUtils.isBlank(flashcardDto.getQuestion().getLangCode()) ||
            StringUtils.isBlank(flashcardDto.getAnswer().getValue()) ||
            StringUtils.isBlank(flashcardDto.getAnswer().getLangCode())) {
            throw new BadRequestException();
        }
        flashcardRepository.findByQuestionValue(flashcardDto.getQuestion().getValue()).ifPresent(f -> {
            throw new ConflictException();
        });

        flashcard.getQuestion().setValue(flashcardDto.getQuestion().getValue());
        flashcard.getAnswer().setValue(flashcardDto.getAnswer().getValue());

        flashcardRepository.save(flashcard);
    }

    public void deleteFlashcardById(final int id) {
        flashcardRepository.findById(id).ifPresentOrElse(flashcard -> {
            if (! flashcard.isUsed()) {
                flashcardRepository.deleteById(id);

            } else {
                //Flashcard is used in quiz, cannot be deleted.
                throw new BadRequestException();
            }
        }, () -> {
            throw new NotFoundException();
        });
    }

    public List<FlashcardForQuizDto> getFlashcardsByLangCodes(final String questionLangCode,
                                                              final String answerLangCode, final String username) {
        final User user = userRepository.findUserByUsername(username).orElseThrow(NotFoundException::new);
        final List<Flashcard> flashcards =
                flashcardRepository.findAllByUserAndQuestionLanguageLangCodeAndAnswerLanguageLangCode(user,
                        questionLangCode, answerLangCode);
        return flashcards.stream().map(FlashcardForQuizDto::createFrom).collect(Collectors.toList());
    }

    public List<FlashcardDto> getFlashcardsByQuestionLangCode(final String questionLangCode, final String username) {
        final User user = userRepository.findUserByUsername(username).orElseThrow(NotFoundException::new);
        final List<Flashcard> flashcards =
                flashcardRepository.findAllByUserAndQuestionLanguageLangCode(user, questionLangCode);
        return flashcards.stream().map(FlashcardDto::createFrom).collect(Collectors.toList());
    }

    public List<FlashcardDto> getFlashcardsByAnswerLangCode(final String answerLangCode, final String username) {
        final User user = userRepository.findUserByUsername(username).orElseThrow(NotFoundException::new);
        final List<Flashcard> flashcards =
                flashcardRepository.findAllByUserAndAnswerLanguageLangCode(user, answerLangCode);
        return flashcards.stream().map(FlashcardDto::createFrom).collect(Collectors.toList());
    }

    public List<FlashcardDto> getFlashcardsByQuestionQuery(final String questionQuery, final String username) {
        final User user = userRepository.findUserByUsername(username).orElseThrow(NotFoundException::new);
        final List<Flashcard> flashcards = flashcardRepository.findAllByUserAndQuestionContaining(user, questionQuery);
        return flashcards.stream().map(FlashcardDto::createFrom).collect(Collectors.toList());
    }

    public List<FlashcardDto> getFlashcardsByQuestionLangCodeAndAnswerLangCodeAndQuestionQuery(
            final String questionLangCode, final String answerLangCode, final String questionQuery,
            final String username) {
        final User user = userRepository.findUserByUsername(username).orElseThrow(NotFoundException::new);
        final List<Flashcard> flashcards =
                flashcardRepository.findAllByUserAndQuestionLangCodeAndAnswerLangCodeAndQuestionContaining(user,
                        questionQuery, questionLangCode, answerLangCode);
        return flashcards.stream().map(FlashcardDto::createFrom).collect(Collectors.toList());
    }

    public List<FlashcardDto> getFlashcardsByQuestionLangCodeAndQuestionQuery(final String questionLangCode,
                                                                              final String questionQuery,
                                                                              final String username) {
        final User user = userRepository.findUserByUsername(username).orElseThrow(NotFoundException::new);
        final List<Flashcard> flashcards =
                flashcardRepository.findAllByUserAndQuestionLangCodeAndQuestionContaining(user, questionQuery,
                        questionLangCode);
        return flashcards.stream().map(FlashcardDto::createFrom).collect(Collectors.toList());
    }

    public List<FlashcardDto> getFlashcardsByAnswerLangCodeAndQuestionQuery(final String answerLangCode,
                                                                            final String questionQuery,
                                                                            final String username) {
        final User user = userRepository.findUserByUsername(username).orElseThrow(NotFoundException::new);
        final List<Flashcard> flashcards =
                flashcardRepository.findAllByUserAndAnswerLangCodeAndQuestionContaining(user, questionQuery,
                        answerLangCode);
        return flashcards.stream().map(FlashcardDto::createFrom).collect(Collectors.toList());
    }

}
