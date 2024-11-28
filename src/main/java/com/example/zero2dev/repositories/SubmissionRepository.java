package com.example.zero2dev.repositories;

import com.example.zero2dev.dtos.SubmissionDTO;
import com.example.zero2dev.models.Submission;
import com.example.zero2dev.storage.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByUserId(Long userId);
    @Query("SELECT s FROM Submission s JOIN s.language l WHERE l.name LIKE %:name%")
    List<Submission> findByLanguageName(@Param("name") String name);
    List<Submission> findByStatus(SubmissionStatus status);
    List<Submission> findByProblemId(Long problemId);
    @Query("SELECT s FROM Submission s WHERE s.memoryUsed = (" +
            "SELECT MIN(s2.memoryUsed) FROM Submission s2)")
    List<Submission> findSubmissionsWithLowestMemoryUsed();

    @Query("SELECT s FROM Submission s WHERE s.executionTime = (" +
            "SELECT MIN(s2.executionTime) FROM Submission s2)")
    List<Submission> findSubmissionsWithLowestExecutionTime();
}
