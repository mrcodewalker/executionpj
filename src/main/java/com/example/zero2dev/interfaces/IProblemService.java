package com.example.zero2dev.interfaces;

import com.example.zero2dev.dtos.ProblemDTO;
import com.example.zero2dev.responses.ProblemResponse;
import com.example.zero2dev.responses.ProblemSolvedResponse;
import com.example.zero2dev.storage.Difficulty;
import com.example.zero2dev.storage.SubmissionStatus;
import org.springframework.data.domain.Page;

public interface IProblemService {
    ProblemResponse createProblem(ProblemDTO problemDTO);
    ProblemResponse updateProblem(Long id, ProblemDTO problemDTO);
    void deleteProblem(Long id);
    ProblemResponse getProblemById(Long id);
    ProblemSolvedResponse searchProblems(ProblemSolvedResponse response,
                                         String title, Difficulty difficult, Long contestId,
                                         Long categoryId, int page, int size);
    void incrementSubmissionCount(Long problemId);
    void incrementAcceptedSubmissionCount(Long problemId, SubmissionStatus status);
    Long totalAccepted(Long problemId);
    Long totalSubmission(Long problemId);
}
