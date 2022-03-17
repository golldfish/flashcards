package com.example.flashcards.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "quiz")
public class Quiz implements Serializable {
    @Id
    @GeneratedValue
    @Column(unique = true)
    private Integer id;

    @NotNull
    @NotBlank
    private String name;

    private Integer score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users", referencedColumnName = "username")
    private User user;

    @OneToMany(mappedBy = "quiz", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<QuizFlashcards> quizFlashcards;

}


