package com.example.zero2dev.repositories;

import com.example.zero2dev.models.TestCases;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCases, Long> {
    List<TestCases> findByProblemIdAndIsActiveOrderByTestCaseOrderAsc(Long problemId, boolean isActive);
    Optional<TestCases> findByProblemIdAndTestCaseOrder(Long problemId, Long testCaseOrder);
    List<TestCases> findByProblemIdOrderByTestCaseOrderAsc(Long problemId);
    long countByProblemIdAndIsActive(Long problemId, boolean isActive);
    @Query("SELECT t FROM TestCases t WHERE t.problem.id = :problemId AND t.isActive = true ORDER BY t.testCaseOrder ASC")
    List<TestCases> findTestCasesByProblemIdAndLimit(@Param("problemId") Long problemId, Pageable pageable);
}
