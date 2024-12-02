package com.example.zero2dev.repositories;

import com.example.zero2dev.models.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
    @Query("SELECT l FROM Language l WHERE l.version = :version")
    Language findByVersion(@Param("version") String version);
    Language findByName(String name);
}
