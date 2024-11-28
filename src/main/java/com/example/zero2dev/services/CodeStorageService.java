package com.example.zero2dev.services;

import com.example.zero2dev.dtos.CodeStorageDTO;
import com.example.zero2dev.dtos.SubmissionDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.interfaces.ICodeStorageService;
import com.example.zero2dev.mapper.CodeStorageMapper;
import com.example.zero2dev.models.CodeStorage;
import com.example.zero2dev.models.Submission;
import com.example.zero2dev.models.User;
import com.example.zero2dev.repositories.CodeStorageRepository;
import com.example.zero2dev.repositories.SubmissionRepository;
import com.example.zero2dev.repositories.UserRepository;
import com.example.zero2dev.responses.CodeStorageResponse;
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
    @Override
    @Transactional
    public CodeStorageResponse createCodeStorage(CodeStorageDTO codeStorageDTO) {
        return this.existsByUserAndSubmission(codeStorageDTO)
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
        List<CodeStorage> codeStorages = codeStorageRepository.findByUserAndSubmissionWithLimit(
                codeStorageDTO.getUserId(),
                codeStorageDTO.getSubmissionId(),
                limit
        );
        return Optional.ofNullable(codeStorages)
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(CodeStorageResponse::fromData)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException("Can not filter list"));
    }

    @Override
    public List<CodeStorageResponse> getCodeStorageByUser(Long userid) {
        return Optional.ofNullable(this.codeStorageRepository.findByUserId(userid))
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(CodeStorageResponse::fromData)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException("Can not find user"));
    }

    @Override
    public void deleteCodeStorageById(Long id) {
        this.codeStorageRepository.deleteById(id);
    }

    @Override
    public CodeStorageResponse getByInfo(CodeStorageDTO codeStorageDTO) {
        return CodeStorageResponse.fromData(this.findExistRecord(codeStorageDTO));
    }

    public Pair<User, Submission> validateData(CodeStorageDTO codeStorageDTO){
        User user = this.userRepository.findById(codeStorageDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Can not find user"));
        Submission submission = this.submissionRepository.findById(codeStorageDTO.getSubmissionId())
                .orElseThrow(() -> new ResourceNotFoundException("Can not find submission"));
        return Pair.of(user, submission);
    }
    public CodeStorage findExistRecord(CodeStorageDTO codeStorageDTO){
        return this.codeStorageRepository.findByUserAndSubmission(
                codeStorageDTO.getUserId(), codeStorageDTO.getSubmissionId())
                .orElseThrow(() -> new ResourceNotFoundException("Can not find exist record"));
    }
    public boolean existsByUserAndSubmission(CodeStorageDTO codeStorageDTO){
        return this.codeStorageRepository.findByUserAndSubmission(
                codeStorageDTO.getUserId(), codeStorageDTO.getSubmissionId()).isPresent();
    }
    public CodeStorage exchangeEntity(CodeStorageDTO codeStorageDTO){
        Pair<User, Submission> data = this.validateData(codeStorageDTO);
        return CodeStorage.builder()
                .sourceCode(codeStorageDTO.getSourceCode())
                .user(data.getFirst())
                .submission(data.getSecond())
                .build();
    }
}
