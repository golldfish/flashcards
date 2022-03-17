package com.example.flashcards.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "answer")
public class Answer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private Integer id;

    @NotBlank
    @NotNull
    private String value;

    @OneToOne(mappedBy = "answer")
    private Flashcard flashcard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "langCode", referencedColumnName = "langCode")
    private Language language;
}
