package com.example.zero2dev.repositories;

import com.example.zero2dev.dtos.SubmissionDTO;
import com.example.zero2dev.models.Submission;
import com.example.zero2dev.storage.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    Optional<Submission> findFirstByUser_IdAndProblem_IdOrderByCreatedAtDesc(Long userId, Long problemId);
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.user.id = :userId AND s.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") SubmissionStatus status);
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.problem.id = :problemId AND s.status = :status")
    Long countByProblemIdAndStatus(@Param("problemId") Long problemId, @Param("status") SubmissionStatus status);
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.problem.id = :problemId")
    Long countSubmissionByProblemId(@Param("problemId") Long problemId);
    @Query("SELECT COUNT(s), SUM(s.problem.points) " +
            "FROM Submission s " +
            "WHERE s.contest.id = :contestId " +
            "AND s.user.id = :userId " +
            "AND s.status = :status")
    List<Object[]> countAndSumByContestAndUser(
            @Param("contestId") Long contestId,
            @Param("userId") Long userId,
            @Param("status") SubmissionStatus status);
    @Query("SELECT s.status, COUNT(s) " +
            "FROM Submission s " +
            "WHERE s.problem.id = :problemId " +
            "GROUP BY s.status")
    List<Object[]> collectProblemGraph(
            @Param("problemId") Long problemId);
}
