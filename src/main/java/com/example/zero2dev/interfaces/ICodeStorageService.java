package com.example.zero2dev.interfaces;

import com.example.zero2dev.dtos.CodeStorageDTO;
import com.example.zero2dev.dtos.SubmissionDTO;
import com.example.zero2dev.models.CodeStorage;
import com.example.zero2dev.models.Submission;
import com.example.zero2dev.models.User;
import com.example.zero2dev.responses.CodeStorageResponse;
import org.springframework.data.util.Pair;

import java.util.List;

public interface ICodeStorageService {
    CodeStorageResponse createCodeStorage(CodeStorageDTO codeStorageDTO);
    CodeStorageResponse updateCodeStorage(CodeStorageDTO codeStorageDTO);
    List<CodeStorageResponse> getCodeStorageBySize(int size, CodeStorageDTO codeStorageDTO);
    List<CodeStorageResponse> getCodeStorageByUser(Long userId);
    void deleteCodeStorageById(Long id);
    CodeStorageResponse getByInfo(CodeStorageDTO codeStorageDTO);
}
