package com.example.zero2dev.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ListTestCaseResponse {
    private ProblemResponse problemResponse;
    @JsonProperty("test_cases")
    private List<TestCasesResponse> testCasesResponse;
}
