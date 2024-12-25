package com.example.zero2dev.responses;

import com.example.zero2dev.storage.SubmissionStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SolvedResponse {
    private Long submissionId;
    private Long problemId;
    private String sourceCode;
    private String status;
    private Long executionTime;
    private Long memoryUsed;
    private Long contestId;
    private String message;
    private Long failedAt;
    private Long totalTest;
    private String languageName;
    private String compilerVersion;
}
