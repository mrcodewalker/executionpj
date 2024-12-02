package com.example.zero2dev.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CompileCodeResponse {
    @JsonProperty("passed")
    public boolean isPassed;
    @JsonProperty("message")
    public String message;
    @JsonProperty("startTime")
    public long startTime;
    @JsonProperty("endTime")
    public long endTime;
    @JsonProperty("executionTime")
    public long executionTime;
    @JsonProperty("timeLimit")
    public boolean timeLimit;
    @JsonProperty("memoryUsed")
    public Long memoryUsed;
}
