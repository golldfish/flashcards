package com.example.flashcards.model;

import lombok.Getter;
import lombok.Setter;
//import org.checkerframework.common.aliasing.qual.Unique;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "language")
public class Language implements Serializable {
    @Id
    @GeneratedValue
    //@Unique
    private Integer id;

    @Column(unique = true, name = "langCode")
    @NotNull
    @NotBlank
    @Size(min = 2, max = 2)
    private String langCode;

    @NotNull
    @NotBlank
    private String name;

    @OneToMany(mappedBy = "language", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answer;

    @OneToMany(mappedBy = "language", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;

}
