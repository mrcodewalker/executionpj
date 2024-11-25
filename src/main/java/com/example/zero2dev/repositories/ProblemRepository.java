package com.example.zero2dev.repositories;

import com.example.zero2dev.models.Problem;
import com.example.zero2dev.storage.Difficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    List<Problem> findByIsActiveTrue();
    List<Problem> findByCategoryId(Long categoryId);
    List<Problem> findByDifficult(Difficulty difficult);

    @Query("SELECT p FROM Problem p WHERE " +
            "(:title IS NULL OR p.title LIKE %:title%) AND " +
            "(:difficult IS NULL OR p.difficult = :difficult) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId)")
    Page<Problem> searchProblems(
            @Param("title") String title,
            @Param("difficult") Difficulty difficult,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );
}
