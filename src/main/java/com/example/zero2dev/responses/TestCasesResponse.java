package com.example.zero2dev.responses;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TestCasesResponse {
    private Long id;
    private String input;
    private String output;
    private LocalDateTime createdAt;
    private Long testCaseOrder;
    private boolean isActive;
}
