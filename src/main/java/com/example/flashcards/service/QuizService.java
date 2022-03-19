package com.example.flashcards.service;

import com.example.flashcards.dto.quiz.*;
import com.example.flashcards.exception.NotFoundException;
import com.example.flashcards.model.Flashcard;
import com.example.flashcards.model.Quiz;
import com.example.flashcards.model.QuizFlashcard;
import com.example.flashcards.model.User;
import com.example.flashcards.repository.FlashcardRepository;
import com.example.flashcards.repository.QuizFlashcardsRepository;
import com.example.flashcards.repository.QuizRepository;
import com.example.flashcards.repository.UserRepository;
import com.example.flashcards.validation.QuizValidator;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class QuizService {

    private final FlashcardRepository flashcardRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final QuizFlashcardsRepository quizFlashcardsRepository;
    private final FlashcardService flashcardService;
    private final QuizValidator quizValidator;

    @Transactional(readOnly = true)
    public List<QuizDto> getAll(final String username) {
        final User user = userRepository.findUserByUsername(username).orElseThrow(NotFoundException::new);
        final List<Quiz> quizzes = quizRepository.findAllByUser(user);
        return quizzes.stream().map(QuizDto::createFrom).collect(Collectors.toList());
    }

    @Transactional
    public void createQuiz(final QuizCreateDto quizCreateDto, final String username) {
        quizValidator.validateQuizCreateParameters(quizCreateDto);

        final User user = userRepository.findUserByUsername(username).orElseThrow(NotFoundException::new);

        final Set<Flashcard> flashcards = quizCreateDto.getFlashcardsId().stream()
                .map(f -> flashcardRepository.findById(f).orElseThrow(NotFoundException::new))
                .peek(flashcard -> {
                    flashcard.setUsed(true);
                    flashcardRepository.save(flashcard);
                })
                .collect(Collectors.toSet());

        final Quiz quiz = Quiz.builder()
                .name(quizCreateDto.getName())
                .user(user)
                .quizFlashcards(new ArrayList<>())
                .build();

        final List<QuizFlashcard> quizFlashcards = new ArrayList<>();
        flashcards.forEach(flashcard -> quizFlashcards.add(buildQuizFlashcard(quiz, flashcard)));
        quizFlashcardsRepository.saveAll(quizFlashcards);
        quiz.setQuizFlashcards(quizFlashcards);
        quizRepository.save(quiz);
    }

    @Transactional
    public void editQuiz(final int id, final QuizEditDto quizEditDto, final String username) {
        quizValidator.validateQuizEditParameters(quizEditDto);
        userRepository.findUserByUsername(username).orElseThrow(NotFoundException::new);

        final Quiz quiz = quizRepository.findById(id).orElseThrow(NotFoundException::new);

        final Set<Integer> currentFlashcardsIds = quizFlashcardsRepository.findFlashcardsIdByQuizId(id);
        final Sets.SetView<Integer> flashcardsIdsToRemove = Sets.difference(currentFlashcardsIds, quizEditDto.getFlashcardsId());
        final Sets.SetView<Integer> flashcardsIdsToAdd = Sets.difference(quizEditDto.getFlashcardsId(), currentFlashcardsIds);

        flashcardsIdsToRemove.forEach(f -> quizFlashcardsRepository.findByQuizIdAndFlashcardId(id, f).ifPresentOrElse(qf -> {
            if (quizFlashcardsRepository.findByFlashcardId(qf.getFlashcard().getId()).isEmpty()
                    || quizFlashcardsRepository.findByFlashcardId(qf.getFlashcard().getId()).contains(qf)) {
                qf.getFlashcard().setUsed(false);
                flashcardRepository.save(qf.getFlashcard());
            }
            quizFlashcardsRepository.deleteById(qf.getId());
        }, NotFoundException::new));

        final List<QuizFlashcard> quizFlashcards = new ArrayList<>();
        flashcardsIdsToAdd.forEach(f -> flashcardRepository.findById(f).ifPresentOrElse(flashcard -> {
            flashcard.setUsed(true);
            flashcardRepository.save(flashcard);
            quizFlashcards.add(buildQuizFlashcard(quiz, flashcard));
        }, NotFoundException::new));

        quizFlashcardsRepository.saveAll(quizFlashcards);
        quiz.setName(quizEditDto.getName());
        if (!flashcardsIdsToAdd.isEmpty() || !flashcardsIdsToRemove.isEmpty()) {
            quiz.setScore(null);
        }
        quizRepository.save(quiz);
    }

    @Transactional(readOnly = true)
    public QuizDetailsDto getQuizDetails(final int id, final String username) {
        userRepository.findUserByUsername(username).orElseThrow(NotFoundException::new);
        final Quiz quiz = quizRepository.findById(id).orElseThrow(NotFoundException::new);
        return QuizDetailsDto.createFrom(quiz, flashcardRepository.findAllByQuizId(id));
    }

    @Transactional
    public void solveQuiz(final Integer id, final List<QuizSolveDto> quizSolveDtos, final String username) {
        quizSolveDtos.forEach(quizValidator::validateQuizSolveParameters);
        userRepository.findUserByUsername(username).orElseThrow(NotFoundException::new);

        final Quiz quiz = quizRepository.findById(id).orElseThrow(NotFoundException::new);

        quizSolveDtos.forEach(qs -> {
            final QuizFlashcard quizFlashcard = quizFlashcardsRepository.findByQuizIdAndFlashcardId(id, qs.getFlashcardId()).orElseThrow(NotFoundException::new);
            quizFlashcard.setUserAnswer(qs.getUserAnswer());
            quizFlashcardsRepository.save(quizFlashcard);
        });

        quiz.setScore(calculateResult(quiz));
        quizRepository.save(quiz);
    }

    @Transactional(readOnly = true)
    public QuizResultDto getResults(final int id, final String username) {
        userRepository.findUserByUsername(username).orElseThrow(NotFoundException::new);
        final Quiz quiz = quizRepository.findById(id).orElseThrow(NotFoundException::new);
        return QuizResultDto.createFrom(quiz, quizFlashcardsRepository.findByQuizId(id));
    }

    public void deleteQuizById(final int id, final String username) {
        userRepository.findUserByUsername(username).orElseThrow(NotFoundException::new);

        quizRepository.findById(id).ifPresentOrElse(quiz -> quiz.getQuizFlashcards().forEach(qf -> {
            if (quizFlashcardsRepository.findByFlashcardId(qf.getFlashcard().getId()).isEmpty()
                    || quizFlashcardsRepository.findByFlashcardId(qf.getFlashcard().getId()).contains(qf)) {
                qf.getFlashcard().setUsed(false);
                flashcardRepository.save(qf.getFlashcard());
            }
            quizRepository.deleteById(id);
        }), NotFoundException::new);
    }

    private QuizFlashcard buildQuizFlashcard(final Quiz quiz, final Flashcard flashcard) {
        return QuizFlashcard.builder()
                .quiz(quiz)
                .flashcard(flashcard)
                .userAnswer(null)
                .build();
    }

    private int calculateResult(final Quiz quiz) {
        final long correct = quizFlashcardsRepository.findByQuizId(quiz.getId()).stream()
                .filter(qf -> qf.getFlashcard().getAnswer().getValue().equalsIgnoreCase(qf.getUserAnswer())).count();

        return ((int) correct * 100) / quiz.getQuizFlashcards().size();
    }
}


