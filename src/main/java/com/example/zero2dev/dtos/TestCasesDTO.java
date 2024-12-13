package com.example.zero2dev.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TestCasesDTO {
    private Long problemId;
    private String input;
    private String output;
    private Boolean isActive;
}