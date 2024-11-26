package com.example.zero2dev.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TestCaseReaderDTO {
    private String inputPath;
    private String outputPath;
    private Long problemId;
    @JsonProperty("isActive")
    private boolean isActive;
}
