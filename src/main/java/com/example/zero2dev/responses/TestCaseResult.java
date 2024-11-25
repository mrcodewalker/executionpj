package com.example.zero2dev.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseResult {
    private boolean passed;
    private String message;
    private long startTime;
    private long endTime;
    private long executionTime;
    private boolean timeLimit;
}