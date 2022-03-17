package com.example.flashcards.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "quiz_flashcards")
public class QuizFlashcards implements Serializable {
    @Id
    @GeneratedValue
    @Column(unique = true)
    private Integer id;

    private String userAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz", referencedColumnName = "id")
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flashcard", referencedColumnName = "id")
    private Flashcard flashcard;


//    @OneToMany(mappedBy = "quizFlashcards", fetch = FetchType.LAZY, orphanRemoval = true)
//    private List<QuizUserAnswers> quizUserAnswers;
}
