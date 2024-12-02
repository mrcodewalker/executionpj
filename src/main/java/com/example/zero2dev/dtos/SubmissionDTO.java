package com.example.zero2dev.dtos;

import com.example.zero2dev.storage.CompilerVersion;
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
    private Long contestId;
    private String sourceCode;
    private String compilerVersion;
}
