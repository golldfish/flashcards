package com.example.flashcards.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "flashcard")
public class Flashcard implements Serializable {
    @Id
    @GeneratedValue
    @Column(unique = true)
    private Integer id;

    @Column(name = "is_used", nullable = false)
    private boolean isUsed;

    @Column(name = "creation_date", nullable = false)
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
    private Set<QuizFlashcard> quizFlashcards;
}
