package com.example.flashcards.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Builder
@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "flashcard")
public class Flashcard implements Serializable {
    @Id
    @GeneratedValue
    @Column(unique = true)
    private Integer id;

    private boolean isUsed;
    private Date creationDate;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "question", referencedColumnName = "id")
    private Question question;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "answer", referencedColumnName = "id")
    private Answer answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users", referencedColumnName = "username")
    private User user;

    @OneToMany(mappedBy = "flashcard", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<QuizFlashcards> quizFlashcards;
}
