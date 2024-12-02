package com.example.zero2dev.interfaces;

import com.example.zero2dev.dtos.SubmissionDTO;
import com.example.zero2dev.responses.SubmissionResponse;

import java.util.List;

public interface ISubmissionService {
    SubmissionResponse createSubmission(SubmissionDTO submissionDTO);
    List<SubmissionResponse> getSubmissionByUserId(Long userId);
    List<SubmissionResponse> getSubmissionByProblemId(Long problemId);
    List<SubmissionResponse> getSubmissionByLanguageName(String name);
    List<SubmissionResponse> getSubmissionByStatus(String status);
    List<SubmissionResponse> getSubmissionLowestMemoryUsed();
    List<SubmissionResponse> getSubmissionByLowestExecutionTime();
    SubmissionResponse deleteSubmissionById(Long id);
    List<SubmissionResponse> deleteSubmissionByUserId(Long userId);
    List<SubmissionResponse> deleteSubmissionByProblemId(Long problemId);
    SubmissionResponse getSubmissionByUserIdAndProblemId(Long userId, Long problemId);
}
