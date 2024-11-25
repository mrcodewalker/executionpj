package com.example.zero2dev.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ListCompileCodeResponse {
    @JsonProperty("list_compile_code")
    private List<CompileCodeResponse> compileCodeResponses;
    private long totalExecutionTime;
    private boolean allTestsPassed;
    private int totalTests;
    private long failedAt;
}
