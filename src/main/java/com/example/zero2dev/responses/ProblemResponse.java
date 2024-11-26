package com.example.zero2dev.responses;

import com.example.zero2dev.storage.Difficulty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProblemResponse {
    private String title;
    private String description;
    private Difficulty difficult;
    private Long categoryId;
    private Long timeLimit;
    private Long totalSubmission;
    private Long acceptedSubmission;
    private Long points;
}