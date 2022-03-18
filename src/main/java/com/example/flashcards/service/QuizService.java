package com.example.flashcards.service;

import com.example.flashcards.dto.quiz.QuizDto;
import com.example.flashcards.exception.NotFoundException;
import com.example.flashcards.model.Answer;
import com.example.flashcards.model.Question;
import com.example.flashcards.model.Quiz;
import com.example.flashcards.model.User;
import com.example.flashcards.repository.FlashcardRepository;
import com.example.flashcards.repository.QuizFlashcardsRepository;
import com.example.flashcards.repository.QuizRepository;
import com.example.flashcards.repository.UserRepository;
import com.flashcards.dao.FlashcardRepository;
import com.flashcards.dao.QuizFlashcardsRepository;
import com.flashcards.dao.QuizRepository;
import com.flashcards.dao.UserRepository;
import com.flashcards.dto.quiz.*;
import com.flashcards.exception.InvalidDataException;
import com.flashcards.exception.NotFoundException;
import com.flashcards.model.*;
import com.flashcards.service.map.FlashcardMapper;
import com.flashcards.service.map.QuizFlashcardMapper;
import com.flashcards.service.map.QuizMapper;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class QuizService {
    private final FlashcardRepository flashcardRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final QuizFlashcardsRepository quizFlashcardsRepository;
    private final FlashcardService flashcardService;

    public List<QuizDto> getQuizzes(final String username) {
        final User user = userRepository.findUserByUsername(username).orElseThrow(NotFoundException::new);
        final List<Quiz> quizzes = quizRepository.findAllByUser(user);
        return quizzes.stream().map(QuizDto::createFrom).collect(Collectors.toList());
    }

    public void createNewQuiz(final QuizCreateDto quizCreateDto, final String username) {
        if (StringUtils.isBlank(quizCreateDto.getName()) || StringUtils.isBlank(quizCreateDto.getAnswerLangCode()) || StringUtils.isBlank(quizCreateDto.getQuestionLangCode()) || quizCreateDto.getFlashcardsIds().isEmpty()) {
            throw new InvalidDataException();
        }
        final User user = userRepository.findUserByUsername(username).orElseThrow(NotFoundException::new);
        final List<Flashcard> flashcards = new ArrayList<>();
        for (Integer flashcardsId : quizCreateDto.getFlashcardsIds()) {
            final Flashcard flashcard = flashcardRepository.findById(flashcardsId).orElseThrow(NotFoundException::new);
            flashcards.add(flashcard);
            flashcardMapper.setFlagForFlashcardEntity(flashcard, true);
            flashcardRepository.save(flashcard);
        }

        final Quiz quiz = quizMapper.convertQuizCreateDtoToEntity(quizCreateDto, user);
        final List<QuizFlashcards> quizFlashcards = new ArrayList<>();
        flashcards.forEach(flashcard -> quizFlashcards.add(quizFlashcardMapper.convertToQuizFlashcardEntity(flashcard, quiz)));


        quizFlashcardsRepository.saveAll(quizFlashcards);
        quizMapper.addQuizFlashcardsToQuiz(quiz, quizFlashcards);
        quizRepository.save(quiz);
    }

    public void editQuiz(final int id, final QuizEditDto quizEditDto) {
        if (StringUtils.isBlank(quizEditDto.getName()) || quizEditDto.getFlashcardsIds().isEmpty()) {
            throw new InvalidDataException();
        }
        final Quiz quiz = quizRepository.findById(id).orElseThrow(NotFoundException::new);
        final Set<Integer> currentFlashcardsIds = returnCurrentFlashcardsIds(quizFlashcardsRepository.findByQuizId(id));
        Sets.SetView<Integer> flashcardsIdsToRemove = Sets.difference(currentFlashcardsIds, quizEditDto.getFlashcardsIds());
        Sets.SetView<Integer> flashcardsIdsToAdd = Sets.difference(quizEditDto.getFlashcardsIds(), currentFlashcardsIds);
        final List<Flashcard> flashcards = new ArrayList<>();
        for (Integer integer : flashcardsIdsToAdd) {
            final Flashcard flashcard = flashcardRepository.findById(integer).orElseThrow(NotFoundException::new);
            flashcards.add(flashcard);
            flashcardMapper.setFlagForFlashcardEntity(flashcard, true);
            flashcardRepository.save(flashcard);
        }

        flashcardsIdsToRemove.forEach(flashcard -> {
            final QuizFlashcards quizFlashcard = quizFlashcardsRepository.findByQuizIdAndFlashcardId(quiz.getId(), flashcard).orElseThrow(NotFoundException::new);
            flashcardMapper.setFlagForFlashcardEntity(quizFlashcard.getFlashcard(), false);
            flashcardRepository.save(quizFlashcard.getFlashcard());
            quizFlashcardsRepository.deleteById(quizFlashcard.getId());
        });

        final List<QuizFlashcards> quizFlashcards = new ArrayList<>();
        flashcards.forEach(flashcard -> quizFlashcards.add(quizFlashcardMapper.convertToQuizFlashcardEntity(flashcard, quiz)));
        quizFlashcardsRepository.saveAll(quizFlashcards);
        quizMapper.updateQuizFromDto(quizEditDto, quiz);
        if (!flashcardsIdsToAdd.isEmpty() || !flashcardsIdsToRemove.isEmpty()) {
            quizMapper.updateQuizScore(quiz, null);
        }
        quizRepository.save(quiz);
    }

    public QuizEditDisplayDto getQuizByIdToDisplayEdit(final int id) {
        final Quiz quiz = quizRepository.findById(id).orElseThrow(NotFoundException::new);
        final List<QuizFlashcards> flashcards = quizFlashcardsRepository.findByQuizId(id);
        return quizMapper.convertToQuizEditDisplayDto(quiz, flashcards);
    }

    public QuizTakeToSolveDto getQuizByIdToSolve(final int id) {
        final Quiz quiz = quizRepository.findById(id).orElseThrow(NotFoundException::new);
        final List<QuizFlashcards> flashcards = quizFlashcardsRepository.findByQuizId(id, Sort.by("flashcard.id"));
        return quizMapper.convertToQuizTakeToSolveDto(quiz, flashcards);
    }

    public void solveQuiz(final Integer id, final QuizSolveDto quizSolveDto) {
        final Quiz quiz = quizRepository.findById(id).orElseThrow(NotFoundException::new);

        final List<QuizSolvedDto> quizSolvedDtos = quizSolveDto.getResults();

        quizSolvedDtos.forEach(quizSolvedDto -> {
            QuizFlashcards quizFlashcard = quizFlashcardsRepository.findByQuizIdAndFlashcardId(id, quizSolvedDto.getFlashcardId()).orElseThrow(NotFoundException::new);
            quizFlashcardMapper.updateQuizFlashcardEntity(quizSolvedDto, quizFlashcard);
            quizFlashcardsRepository.save(quizFlashcard);
        });
        quizMapper.updateQuizScore(quiz, calculateResult(quiz));
        quizRepository.save(quiz);
    }

    public QuizResultDto getResults(final int id) {
        final Quiz quiz = quizRepository.findById(id).orElseThrow(NotFoundException::new);
        final List<QuizFlashcards> flashcards = quizFlashcardsRepository.findByQuizId(id, Sort.by("flashcard.id"));
        return quizMapper.convertToQuizResultDto(quiz, flashcards);
    }

    public void deleteQuizById(final int id) {
        quizRepository.findById(id).ifPresentOrElse(flashcard -> {
            final List<QuizFlashcards> quizFlashcards = quizRepository.findById(id).get().getQuizFlashcards();
            quizFlashcards.forEach(quizFlashcard -> {
                flashcardMapper.setFlagForFlashcardEntity(quizFlashcard.getFlashcard(), false);
                flashcardRepository.save(quizFlashcard.getFlashcard());
            });
            quizRepository.deleteById(id);
        }, () -> {
            throw new NotFoundException();
        });

    }

    private Set<Integer> returnCurrentFlashcardsIds(final List<QuizFlashcards> flashcards) {
        final Set<Integer> ids = new HashSet<>();
        flashcards.forEach(flashcard -> ids.add(flashcard.getFlashcard().getId()));
        return ids;
    }

    private Integer calculateResult(final Quiz quiz) {
        AtomicInteger correctAnswers = new AtomicInteger();
        List<QuizFlashcards> flashcards = quizFlashcardsRepository.findByQuizId(quiz.getId());
        flashcards.forEach(flashcard -> {
            if (compareAnswers(flashcard.getFlashcard().getAnswer().getName(), flashcard.getUserAnswer())) {
                correctAnswers.addAndGet(1);
            }
        });
        return (correctAnswers.get() * 100) / quiz.getQuizFlashcards().size();
    }

    private boolean compareAnswers(final String correctAnswer, final String userAnswer) {
        return correctAnswer.equalsIgnoreCase(userAnswer);
    }


}


