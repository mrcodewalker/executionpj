package com.example.zero2dev.services;

import com.example.zero2dev.dtos.CodeStorageDTO;
import com.example.zero2dev.dtos.SubmissionDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.interfaces.ICodeStorageService;
import com.example.zero2dev.mapper.CodeStorageMapper;
import com.example.zero2dev.models.CodeStorage;
import com.example.zero2dev.models.Problem;
import com.example.zero2dev.models.Submission;
import com.example.zero2dev.models.User;
import com.example.zero2dev.repositories.CodeStorageRepository;
import com.example.zero2dev.repositories.ProblemRepository;
import com.example.zero2dev.repositories.SubmissionRepository;
import com.example.zero2dev.repositories.UserRepository;
import com.example.zero2dev.responses.CodeStorageResponse;
import com.example.zero2dev.storage.MESSAGE;
import com.example.zero2dev.storage.SubmissionStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CodeStorageService implements ICodeStorageService {
    private final CodeStorageMapper mapper;
    private final CodeStorageRepository codeStorageRepository;
    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final ProblemRepository problemRepository;
    @Override
    @Transactional
    public CodeStorageResponse createCodeStorage(CodeStorageDTO codeStorageDTO) {
        return this.existsByUserAndProblem(codeStorageDTO)
                ? this.updateCodeStorage(codeStorageDTO)
                : CodeStorageResponse.fromData(codeStorageRepository.save(this.exchangeEntity(codeStorageDTO)));
    }
    @Override
    public CodeStorageResponse updateCodeStorage(CodeStorageDTO codeStorageDTO) {
        CodeStorage existingRecord = this.findExistRecord(codeStorageDTO);
        existingRecord.setSourceCode(codeStorageDTO.getSourceCode());
        return CodeStorageResponse.fromData(this.codeStorageRepository.save(existingRecord));
    }

    @Override
    public List<CodeStorageResponse> getCodeStorageBySize(int size, CodeStorageDTO codeStorageDTO) {
        Pageable limit = PageRequest.of(0, size);
        List<CodeStorage> codeStorages = codeStorageRepository.findByUserAndProblemWithLimit(
                codeStorageDTO.getUserId(),
                codeStorageDTO.getProblemId(),
                limit
        );
        return Optional.ofNullable(codeStorages)
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(CodeStorageResponse::fromData)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.ARRAY_SIZE_ERROR));
    }

    @Override
    public List<CodeStorageResponse> getCodeStorageByUser(Long userid) {
        return Optional.ofNullable(this.codeStorageRepository.findByUserId(userid))
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(CodeStorageResponse::fromData)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.ARRAY_SIZE_ERROR));
    }

    @Override
    public void deleteCodeStorageById(Long id) {
        this.codeStorageRepository.deleteById(id);
    }

    @Override
    public CodeStorageResponse getByInfo(CodeStorageDTO codeStorageDTO) {
        return CodeStorageResponse.fromData(this.findExistRecord(codeStorageDTO));
    }

    @Override
    public CodeStorageResponse findCodeStorageAccepted(Long userId, Long problemId, SubmissionStatus status) {
        return CodeStorageResponse.fromData(
                this.codeStorageRepository.findByUserAndProblem(userId, problemId)
                        .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION)));
    }

    public Pair<User, Problem> validateData(CodeStorageDTO codeStorageDTO){
        User user = this.userRepository.findById(codeStorageDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
        Problem problem = this.problemRepository.findById(codeStorageDTO.getProblemId())
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
        return Pair.of(user, problem);
    }
    public CodeStorage findExistRecord(CodeStorageDTO codeStorageDTO){
        return this.codeStorageRepository.findByUserAndProblem(
                codeStorageDTO.getUserId(), codeStorageDTO.getProblemId())
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
    public boolean existsByUserAndProblem(CodeStorageDTO codeStorageDTO){
        return this.codeStorageRepository.findByUserAndProblem(
                codeStorageDTO.getUserId(), codeStorageDTO.getProblemId()).isPresent();
    }
    public CodeStorage exchangeEntity(CodeStorageDTO codeStorageDTO){
        Pair<User, Problem> data = this.validateData(codeStorageDTO);
        return CodeStorage.builder()
                .sourceCode(codeStorageDTO.getSourceCode())
                .user(data.getFirst())
                .problem(data.getSecond())
                .build();
    }
}
