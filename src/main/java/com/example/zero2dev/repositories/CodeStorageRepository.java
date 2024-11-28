package com.example.zero2dev.repositories;

import com.example.zero2dev.models.CodeStorage;
import com.example.zero2dev.models.Submission;
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
    List<CodeStorage> findBySubmissionId(Long submissionId);
    @Query("SELECT c FROM CodeStorage c " +
            "WHERE c.user.id = :userId AND c.submission.id = :submissionId")
    Optional<CodeStorage> findByUserAndSubmission(@Param("userId") Long userId,
                                                 @Param("submissionId") Long submissionId);
    @Query("SELECT c FROM CodeStorage c WHERE c.user.id = :userId AND c.submission.id = :submissionId")
    List<CodeStorage> findByUserAndSubmissionWithLimit(@Param("userId") Long userId,
                                                       @Param("submissionId") Long submissionId,
                                                       Pageable pageable);
}
