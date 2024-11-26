package com.example.zero2dev.responses;

import com.example.zero2dev.dtos.ProblemDTO;
import com.example.zero2dev.mapper.ProblemMapper;
import com.example.zero2dev.mapper.ProblemMapperImpl;
import com.example.zero2dev.mapper.TestCaseReaderMapper;
import com.example.zero2dev.mapper.TestCaseReaderMapperImpl;
import com.example.zero2dev.models.Problem;
import com.example.zero2dev.models.TestCaseReader;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ListTestCaseReaderResponse {
    @JsonProperty("test_case")
    private List<TestCaseResponse> testCaseResponseList;
    @JsonProperty("problem")
    private ProblemResponse problemResponse;
    public static ListTestCaseReaderResponse exchangeEntity(List<TestCaseReader> list, Problem problem){
        TestCaseReaderMapper mapper = new TestCaseReaderMapperImpl();
        return ListTestCaseReaderResponse.builder()
                .testCaseResponseList(mapper.toResponseList(list))
                .problemResponse(ProblemDTO.exchangeEntity(problem))
                .build();
    }
}
