package com.example.zero2dev.repositories;

import com.example.zero2dev.dtos.SubmissionDTO;
import com.example.zero2dev.dtos.UserRankingDTO;
import com.example.zero2dev.models.Submission;
import com.example.zero2dev.storage.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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
    Optional<Submission> findFirstByUser_IdAndProblem_IdAndContest_IdOrderByCreatedAtDesc(Long userId, Long problemId, Long contestId);
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
            "SUM(s.execution_time) AS exec_time, SUM(s.memory_used) AS mem_used, u.avatar_url " +
            "FROM submission s " +
            "LEFT JOIN `user` u ON s.user_id = u.id " +
            "LEFT JOIN problem p ON s.problem_id = p.id " +
            "WHERE s.status = :status AND s.contest_id = :contestId " +
            "GROUP BY s.contest_id,s.user_id,u.username " +
            "ORDER BY total_score DESC, exec_time, mem_used ASC;", nativeQuery = true)
    List<Object[]> getContestRankingByContestId(
            @Param("contestId") Long contestId,
            @Param("status") String status);
    @Query(value = "SELECT c.user_id, c.problem_id, " +
            "c.source_code, s.execution_time, s.memory_used, s.message, s.failed_at, s.total_test, " +
            "s.`status`, l.`name`, l.`version`, s.id, s.contest_id " +
            "FROM submission s " +
            "JOIN `language` l ON l.id = s.id " +
            "JOIN code_storage c ON s.problem_id = c.problem_id AND s.`status` = :status " +
            "WHERE c.user_id = :userId AND c.problem_id = :problemId AND s.`status` = :status " +
            "GROUP BY c.user_id, c.problem_id;", nativeQuery = true)
    List<Object[]> getDetailSubmissionByUserIdAndProblemId(
            @Param("userId") Long userId,
            @Param("problemId") Long problemId,
            @Param("status") String status);
    @Query("SELECT u, p, l, c FROM User u " +
            "JOIN Problem p ON p.id = :problemId " +
            "JOIN Language l ON l.version = :compilerVersion " +
            "JOIN Contest c ON c.id = :contestId " +
            "WHERE u.id = :userId " +
            "AND u.isActive = true " +
            "AND p.isActive = true " +
            "AND l.isActive = true")
    List<Object[]> findSubmissionValidationData(
            @Param("userId") Long userId,
            @Param("problemId") Long problemId,
            @Param("contestId") Long contestId,
            @Param("compilerVersion") String compilerVersion
    );
    @Query(
            value = """
        SELECT DISTINCT s.id, 
                        s.user_id, 
                        s.problem_id, 
                        c.source_code, 
                        s.language_id, 
                        s.`status`, 
                        s.execution_time, 
                        s.memory_used, 
                        s.contest_id, 
                        s.message, 
                        s.failed_at, 
                        s.total_test, 
                        l.name, 
                        l.`version`
        FROM submission s
        JOIN code_storage c 
            ON c.user_id = s.user_id
        JOIN language l 
            ON l.id = s.language_id
        WHERE s.user_id = :userId 
          AND s.contest_id = :contestId
    """,
            nativeQuery = true
    )
    List<Object[]> getProblemSolved(
            @Param("userId") Long userId,
            @Param("contestId") Long contestId
    );
    @Query(value = """
            WITH RankedUsers AS (
                SELECT 
                    RANK() OVER (ORDER BY SUM(p.points) DESC, SUM(s.execution_time), SUM(s.memory_used) ASC) AS ranking,
                    s.user_id AS userId,
                    u.username AS username,
                    u.avatar_url AS avatarUrl,
                    SUM(s.execution_time) AS totalExecutionTime,
                    SUM(s.memory_used) AS totalMemoryUsed,
                    SUM(p.points) AS totalPoints
                FROM 
                    submission s
                JOIN 
                    problem p ON p.id = s.problem_id
                JOIN 
                    user u ON u.id = s.user_id
                WHERE 
                    s.status = 'ACCEPTED'
                GROUP BY 
                    s.user_id, u.username
            )
            SELECT * FROM RankedUsers
            ORDER BY ranking
            LIMIT :limit OFFSET :offset
            """, nativeQuery = true)
    List<Object[]> findUserRankings(@Param("limit") int limit, @Param("offset") int offset);

    @Query(value = """
            SELECT COUNT(DISTINCT s.user_id)
            FROM submission s
            WHERE s.status = 'ACCEPTED'
            """, nativeQuery = true)
    long countUserRankings();
}
