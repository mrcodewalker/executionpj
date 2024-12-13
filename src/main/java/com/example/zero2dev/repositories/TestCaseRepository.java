package com.example.zero2dev.repositories;

import com.example.zero2dev.models.TestCases;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCases, Long> {
    List<TestCases> findByProblemIdAndIsActiveOrderByTestCaseOrderAsc(Long problemId, boolean isActive);
    Optional<TestCases> findByProblemIdAndTestCaseOrder(Long problemId, Long testCaseOrder);
    List<TestCases> findByProblemIdOrderByTestCaseOrderAsc(Long problemId);
    long countByProblemIdAndIsActive(Long problemId, boolean isActive);
}
