package com.example.zero2dev.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CodeStorageDTO {
    private String sourceCode;
    private Long problemId;
    private Long userId;
}
