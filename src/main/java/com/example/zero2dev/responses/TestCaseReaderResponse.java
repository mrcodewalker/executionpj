package com.example.zero2dev.responses;

import com.example.zero2dev.dtos.ProblemDTO;
import com.example.zero2dev.models.TestCaseReader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TestCaseReaderResponse {
    @JsonProperty("test_case")
    private TestCaseResponse testCaseResponse;
    @JsonProperty("problem")
    private ProblemResponse problemResponse;
    public static TestCaseReaderResponse exchangeEntity(
            TestCaseReader testCaseReader){
        return TestCaseReaderResponse.builder()
                .testCaseResponse(
                        TestCaseResponse.builder()
                                .id(testCaseReader.getId())
                                .inputPath(testCaseReader.getInputPath())
                                .outputPath(testCaseReader.getOutputPath())
                                .isActive(testCaseReader.getIsActive())
                                .build())
                .problemResponse(
                        ProblemDTO.exchangeEntity(testCaseReader.getProblem()))
                .build();
    }
}
