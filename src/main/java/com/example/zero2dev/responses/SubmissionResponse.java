package com.example.zero2dev.responses;

import com.example.zero2dev.models.Language;
import com.example.zero2dev.models.Problem;
import com.example.zero2dev.models.Submission;
import com.example.zero2dev.storage.SubmissionStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionResponse {
    private Long id;
    private Long userId;
    private Long contestId;
    private Problem problem;
    private SubmissionStatus status;
    private Long failedAt;
    private String message;
    private Long executionTime;
    private Long memoryUsed;
    private Long totalTest;
    private String sourceCode;
    private String detailMessage;
    private String compilerVersion;
    private String languageName;
    private boolean allTestPassed;
    private Long gems;
    @JsonProperty("list_compile_code")
    private List<CompileCodeResponse> compileCodeResponses;
    public static SubmissionResponse exchangeEntity(Submission submission){
        return SubmissionResponse.builder()
                .id(submission.getId())
                .userId(submission.getUser().getId())
                .executionTime(submission.getExecutionTime())
                .message(submission.getMessage())
                .totalTest(submission.getTotalTest())
                .failedAt(submission.getFailedAt())
                .contestId(submission.getContest().getId())
                .languageName(submission.getLanguage().getName())
                .compilerVersion(submission.getLanguage().getVersion())
                .memoryUsed(submission.getMemoryUsed())
                .status(submission.getStatus())
                .problem(submission.getProblem())
                .build();
    }
}
