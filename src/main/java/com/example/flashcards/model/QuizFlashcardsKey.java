package com.example.flashcards.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class QuizFlashcardsKey implements Serializable {

    @Column(name="quiz_id", nullable = false)
    Integer quizId;

    @Column(name="flashcard_id", nullable = false)
    Integer flashcardId;

}
