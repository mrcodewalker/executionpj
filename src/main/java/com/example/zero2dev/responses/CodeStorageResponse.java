package com.example.zero2dev.responses;

import com.example.zero2dev.dtos.CodeStorageDTO;
import com.example.zero2dev.models.CodeStorage;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CodeStorageResponse {
    private Long id;
    private Long submissionId;
    private Long userId;
    private String sourceCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public static CodeStorageResponse exchangeEntity(CodeStorageDTO codeStorageDTO){
        return CodeStorageResponse.builder()
                .sourceCode(codeStorageDTO.getSourceCode())
                .userId(codeStorageDTO.getUserId())
                .submissionId(codeStorageDTO.getSubmissionId())
                .build();
    }
    public static CodeStorageResponse fromData(CodeStorage codeStorage){
        return CodeStorageResponse.builder()
                .id(codeStorage.getId())
                .sourceCode(codeStorage.getSourceCode())
                .userId(codeStorage.getUser().getId())
                .submissionId(codeStorage.getSubmission().getId())
                .createdAt(codeStorage.getCreatedAt())
                .updatedAt(codeStorage.getUpdatedAt())
                .build();
    }
}
