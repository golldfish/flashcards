package com.example.flashcards.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "quiz_flashcard")
public class QuizFlashcard implements Serializable {

    @EmbeddedId
    private QuizFlashcardsKey id;

    @ManyToOne
    @MapsId("quizId")
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ManyToOne
    @MapsId("flashcardId")
    @JoinColumn(name = "flashcard_id")
    private Flashcard flashcard;

    private String userAnswer;
}

