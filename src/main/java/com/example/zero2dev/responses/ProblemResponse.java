package com.example.zero2dev.responses;

import com.example.zero2dev.models.Category;
import com.example.zero2dev.storage.Difficulty;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProblemResponse {
    private Long id;
    private String title;
    private String description;
    private Difficulty difficult;
    private Category category;
    private Long timeLimit;
    private Long totalSubmission;
    private Long acceptedSubmission;
    private Long points;
    private String tag;
    private Long memoryLimit;
    private String constraints;
    private String inputFormat;
    private String outputFormat;
    @JsonProperty("test_cases")
    private List<ExampleTestCasesResponse> list;
}
