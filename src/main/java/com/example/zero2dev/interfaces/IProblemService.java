package com.example.zero2dev.interfaces;

import com.example.zero2dev.dtos.ProblemDTO;
import com.example.zero2dev.responses.ProblemResponse;
import com.example.zero2dev.storage.Difficulty;
import org.springframework.data.domain.Page;

public interface IProblemService {
    ProblemResponse createProblem(ProblemDTO problemDTO);
    ProblemResponse updateProblem(Long id, ProblemDTO problemDTO);
    void deleteProblem(Long id);
    ProblemResponse getProblemById(Long id);
    Page<ProblemResponse> searchProblems(String title, Difficulty difficult,
                                    Long categoryId, int page, int size);
    void incrementSubmissionCount(Long problemId);
    void incrementAcceptedSubmissionCount(Long problemId);
}
