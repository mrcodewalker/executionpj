package com.example.zero2dev.dtos;

import com.example.zero2dev.storage.SubmissionStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SubmissionDTO {
    private Long userId;
    private Long problemId;
    private Long languageId;
    private Long contestId;
    private String status;
    private Long executionTime;
    private Long memoryUsed;
    private String sourceCode;
}
