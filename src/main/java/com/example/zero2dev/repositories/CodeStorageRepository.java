package com.example.zero2dev.repositories;

import com.example.zero2dev.models.CodeStorage;
import com.example.zero2dev.models.Submission;
import com.example.zero2dev.storage.SubmissionStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeStorageRepository extends JpaRepository<CodeStorage, Long> {
    List<CodeStorage> findByUserId(Long userId);
    List<CodeStorage> findByProblemId(Long problemId);
    @Query("SELECT c FROM CodeStorage c " +
            "WHERE c.user.id = :userId AND c.problem.id = :problemId")
    Optional<CodeStorage> findByUserAndProblem(@Param("userId") Long userId,
                                                 @Param("problemId") Long problemId);
    @Query("SELECT c FROM CodeStorage c WHERE c.user.id = :userId AND c.problem.id = :problemId")
    List<CodeStorage> findByUserAndProblemWithLimit(@Param("userId") Long userId,
                                                       @Param("problemId") Long problemId,
                                                       Pageable pageable);
}
