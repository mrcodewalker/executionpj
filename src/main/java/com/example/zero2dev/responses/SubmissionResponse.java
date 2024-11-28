package com.example.zero2dev.responses;

import com.example.zero2dev.models.Submission;
import com.example.zero2dev.storage.SubmissionStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionResponse {
    private Long id;
    private Long userId;
    private Long contestId;
    private Long problemId;
    private Long languageId;
    private String status;
    private Long executionTime;
    private Long memoryUsed;
    public static SubmissionResponse exchangeEntity(Submission submission){
        return SubmissionResponse.builder()
                .id(submission.getId())
                .userId(submission.getUser().getId())
                .executionTime(submission.getExecutionTime())
                .contestId(submission.getContest().getId())
                .languageId(submission.getLanguage().getId())
                .memoryUsed(submission.getMemoryUsed())
                .status(String.valueOf(submission.getStatus()))
                .problemId(submission.getProblem().getId())
                .build();
    }
}
