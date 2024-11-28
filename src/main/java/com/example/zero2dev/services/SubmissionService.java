package com.example.zero2dev.services;

import com.example.zero2dev.dtos.CodeStorageDTO;
import com.example.zero2dev.dtos.SubmissionDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.interfaces.ISubmissionService;
import com.example.zero2dev.mapper.SubmissionMapper;
import com.example.zero2dev.models.*;
import com.example.zero2dev.repositories.*;
import com.example.zero2dev.responses.LanguageResponse;
import com.example.zero2dev.responses.ProblemResponse;
import com.example.zero2dev.responses.SubmissionResponse;
import com.example.zero2dev.storage.SubmissionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubmissionService implements ISubmissionService {
    private final SubmissionRepository submissionRepository;
    private final SubmissionMapper mapper;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final LanguageRepository languageRepository;
    private final ContestRepository contestRepository;
    private final CodeStorageService codeStorageService;
    @Override
    public SubmissionResponse createSubmission(SubmissionDTO submissionDTO) {
        this.checkValidSubmission(submissionDTO);
        Submission submission = mapper.toEntity(submissionDTO);
        SubmissionResponse response = SubmissionResponse.exchangeEntity(
                this.submissionRepository.save(
                        this.exchangeEntity(submission, submissionDTO)));
        this.codeStorageService.createCodeStorage(this.parseDTO(
                submission, submissionDTO.getSourceCode()));
        return response;
    }
    @Override
    public List<SubmissionResponse> getSubmissionByUserId(Long userId) {
        return Optional.ofNullable(this.submissionRepository.findByUserId(userId))
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(SubmissionResponse::exchangeEntity)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException("Can not find submission with user id"));
    }

    @Override
    public List<SubmissionResponse> getSubmissionByProblemId(Long problemId) {
        return Optional.ofNullable(this.submissionRepository.findByProblemId(problemId))
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(SubmissionResponse::exchangeEntity)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException("Can not find list submission with problem id"));
    }

    @Override
    public List<SubmissionResponse> getSubmissionByLanguageName(String name) {
        return Optional.ofNullable(this.submissionRepository.findByLanguageName(name))
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(SubmissionResponse::exchangeEntity)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException("Can not find list submission with language"));
    }

    @Override
    public List<SubmissionResponse> getSubmissionByStatus(String status) {
        return Optional.ofNullable(this.submissionRepository.findByStatus(SubmissionStatus.valueOf(status)))
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(SubmissionResponse::exchangeEntity)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException("Can not find list with that status"));
    }

    @Override
    public List<SubmissionResponse> getSubmissionLowestMemoryUsed() {
        return Optional.ofNullable(this.submissionRepository.findSubmissionsWithLowestMemoryUsed())
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(SubmissionResponse::exchangeEntity)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException("Can not find list submission lowest memory used"));
    }
    @Override
    public List<SubmissionResponse> getSubmissionByLowestExecutionTime() {
        return Optional.ofNullable(this.submissionRepository.findSubmissionsWithLowestExecutionTime())
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(SubmissionResponse::exchangeEntity)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException("Can not find list submission lowest execution time"));
    }

    @Override
    public SubmissionResponse deleteSubmissionById(Long id) {
        Submission submission = this.getSubmissionById(id);
        this.submissionRepository.delete(submission);
        return SubmissionResponse.exchangeEntity(submission);
    }

    @Override
    public List<SubmissionResponse> deleteSubmissionByUserId(Long userId) {
        return Optional.ofNullable(this.submissionRepository.findByUserId(userId))
                .filter(submissions -> !submissions.isEmpty())
                .map(submissions -> {
                    List<SubmissionResponse> responses = submissions.stream()
                            .map(SubmissionResponse::exchangeEntity)
                            .collect(Collectors.toList());

                    this.submissionRepository.deleteAll(submissions);

                    return responses;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find list submission with user id"));
    }

    @Override
    public List<SubmissionResponse> deleteSubmissionByProblemId(Long problemId) {
        return Optional.ofNullable(this.submissionRepository.findByProblemId(problemId))
                .filter(items -> !items.isEmpty())
                .map(items -> {
                    List<SubmissionResponse> responses = items.stream()
                            .map(SubmissionResponse::exchangeEntity)
                            .collect(Collectors.toList());

                    this.submissionRepository.deleteAll(items);

                    return responses;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Can not find list submission with problem id"));
    }
    public Submission getSubmissionById(Long id){
        return this.submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can not find submission with id"));
    }
    public void checkValidSubmission(SubmissionDTO submissionDTO){
        User user = this.userRepository.findById(submissionDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Can not find with user id"));
        if (!user.getIsActive()){
            throw new ResourceNotFoundException("Can not find user");
        }
        Problem problem = this.problemRepository.findById(submissionDTO.getProblemId())
                .orElseThrow(() -> new ResourceNotFoundException("Can not find with problem id"));
        if (!problem.getIsActive()){
            throw new ResourceNotFoundException("Can not find problem");
        }
        Language language = this.languageRepository.findById(submissionDTO.getLanguageId())
                .orElseThrow(() -> new ResourceNotFoundException("Can not find with language id"));
        if (!language.getIsActive()){
            throw new ResourceNotFoundException("Language is not active");
        }
    }
    public Language getLanguage(Long id){
        return this.languageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can not find language"));
    }
    public User getUser(Long id){
        return this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can not find user"));
    }
    public Problem getProblem(Long id){
        return this.problemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can not find problem"));
    }
    private Contest getContest(Long id){
        return this.contestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can not find contest"));
    }
    private CodeStorageDTO parseDTO(Submission submission, String sourceCode){
        return CodeStorageDTO.builder()
                .submissionId(submission.getId())
                .userId(submission.getUser().getId())
                .sourceCode(sourceCode)
                .build();
    }
    private Submission exchangeEntity(Submission submission, SubmissionDTO submissionDTO){
        submission.setContest(this.getContest(submissionDTO.getContestId()));
        submission.setStatus(SubmissionStatus.valueOf(submissionDTO.getStatus()));
        submission.setLanguage(this.getLanguage(submissionDTO.getLanguageId()));
        submission.setProblem(this.getProblem(submissionDTO.getProblemId()));
        submission.setUser(this.getUser(submissionDTO.getUserId()));
        return submission;
    }
}
