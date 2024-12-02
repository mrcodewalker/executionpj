package com.example.zero2dev.dtos;

import com.example.zero2dev.models.TestCase;
import com.example.zero2dev.storage.CompilerVersion;
import com.example.zero2dev.storage.Language;
import lombok.*;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CompileCodeDTO {
    private String sourceCode;
    private List<TestCase> testCases;
    private Language language;
    private CompilerVersion compilerVersion;
    private Long timeLimit;
}
