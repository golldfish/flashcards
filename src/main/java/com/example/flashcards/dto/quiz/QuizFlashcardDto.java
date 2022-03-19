package com.example.flashcards.dto.quiz;
import com.example.flashcards.dto.flashcard.QuestionDto;
import com.example.flashcards.model.Flashcard;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@Data
@Builder
@RequiredArgsConstructor
public class QuizFlashcardDto {
    Integer id;
    QuestionDto question;

    public static QuizFlashcardDto createFrom(final Flashcard flashcard){
        return QuizFlashcardDto.builder()
                .id(flashcard.getId())
                .question(QuestionDto.createFrom(flashcard.getQuestion()))
                .build();
    }

}
