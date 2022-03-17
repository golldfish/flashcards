package com.example.flashcards.repository;

import com.example.flashcards.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language, Integer> {

    Optional<Language> findByLangCode(final String langCode);

    void deleteByLangCode(final String langCode);
}
