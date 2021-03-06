package com.example.flashcards.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "language")
public class Language implements Serializable {
    @Id
    @GeneratedValue
    //@Unique
    private Integer id;

    @Column(name = "lang_code", unique = true, nullable = false)
    @NotNull
    @NotBlank
    @Size(min = 3, max = 3)
    private String langCode;

    @NotNull
    @NotBlank
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "language", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Answer> answer;

    @OneToMany(mappedBy = "language", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Question> questions;

}
