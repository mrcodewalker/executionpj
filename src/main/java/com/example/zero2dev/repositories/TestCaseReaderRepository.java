package com.example.zero2dev.repositories;

import com.example.zero2dev.models.TestCaseReader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestCaseReaderRepository extends JpaRepository<TestCaseReader, Long> {
    @Query("SELECT tcr FROM TestCaseReader tcr WHERE tcr.problem.id = :problemId AND tcr.isActive = true")
    List<TestCaseReader> getByProblemId(@Param("problemId") Long problemId);
    List<TestCaseReader> findByProblem_IdAndIsActiveTrue(Long problemId);
}
