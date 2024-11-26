package com.example.zero2dev.responses;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TestCaseResponse {
    private Long id;
    private String inputPath;
    private String outputPath;
    private boolean isActive;
}
