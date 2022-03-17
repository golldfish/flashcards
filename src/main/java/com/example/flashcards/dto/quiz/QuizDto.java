package com.example.flashcards.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class  QuizDto {
    private Integer id;
    private String name;
    private Integer score;
    private String questionLangCode;
    private String answerLangCode;

}


