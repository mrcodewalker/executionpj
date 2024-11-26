package com.example.zero2dev.repositories;

import com.example.zero2dev.models.TestCaseReader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestCaseReaderRepository extends JpaRepository<TestCaseReader, Long> {
    List<TestCaseReader> getByProblemId(Long problemId);
}
