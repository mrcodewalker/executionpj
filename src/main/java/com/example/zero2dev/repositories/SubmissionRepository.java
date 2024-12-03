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
    @Query(value = "SELECT s.contest_id,s.user_id,u.username, SUM(p.points) AS total_score," +
            "SUM(s.execution_time) AS exec_time, SUM(s.memory_used) AS mem_used " +
            "FROM submission s " +
            "LEFT JOIN `user` u ON s.user_id = u.id " +
            "LEFT JOIN problem p ON s.problem_id = p.id " +
            "WHERE s.status = :status AND s.contest_id = :contestId " +
            "GROUP BY s.contest_id,s.user_id,u.username " +
            "ORDER BY total_score DESC, exec_time, mem_used ASC;", nativeQuery = true)
    List<Object[]> getContestRankingByContestId(
            @Param("contestId") Long contestId,
            @Param("status") String status);
//    @Query(value = "SELECT s.contest_id, s.user_id, COALESCE(u.username, 'Unknown') AS username, " +
//            "COALESCE(SUM(p.points), 0) AS total_score, " +
//            "COALESCE(SUM(s.execution_time), 0) AS exec_time, " +
//            "COALESCE(SUM(s.memory_used), 0) AS mem_used " +
//            "FROM submission s " +
//            "LEFT JOIN user u ON s.user_id = u.id " +
//            "LEFT JOIN problem p ON s.problem_id = p.id " +
//            "WHERE s.status = :status AND s.contest_id = :contestId " +
//            "GROUP BY s.contest_id, s.user_id, u.username " +
//            "ORDER BY total_score DESC, exec_time ASC, mem_used ASC", nativeQuery = true)
//    List<Object[]> getContestRankingByContestId(
//            @Param("contestId") Long contestId,
//            @Param("status") SubmissionStatus status);
}
