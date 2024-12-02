package com.example.zero2dev.services;

import com.example.zero2dev.controllers.SubmissionController;
import com.example.zero2dev.dtos.CodeStorageDTO;
import com.example.zero2dev.dtos.CompileCodeDTO;
import com.example.zero2dev.dtos.SubmissionDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.interfaces.ISubmissionService;
import com.example.zero2dev.mapper.SubmissionMapper;
import com.example.zero2dev.models.*;
import com.example.zero2dev.repositories.*;
import com.example.zero2dev.responses.*;
import com.example.zero2dev.storage.CompilerVersion;
import com.example.zero2dev.storage.MESSAGE;
import com.example.zero2dev.storage.SubmissionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
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
    private final TestCaseReaderService testCaseReaderService;
    private final CompileCodeService compileCodeService;
    private final ProblemService problemService;
    private final ContestService contestService;
    private final UserService userService;
    @Override
    public SubmissionResponse createSubmission(SubmissionDTO submissionDTO) {
        contestService.findContestById(submissionDTO.getContestId());
        Optional<Submission> existingSubmission = this.getLatestSubmission(submissionDTO.getUserId(), submissionDTO.getProblemId());
        Submission submission = mapper.toEntity(submissionDTO);
        submission.setId(existingSubmission.map(Submission::getId).orElse(null));
        submission.setCreatedAt(existingSubmission.map(Submission::getCreatedAt).orElse(LocalDateTime.now()));
        Pair<Pair<User, Problem>, Language> data = this.checkValidSubmission(submissionDTO);
        Language language = this.getLanguageByCompilerVersion(submissionDTO.getCompilerVersion());
        Problem problem = data.getFirst().getSecond();
        CompileCodeDTO compileCodeDTO = CompileCodeDTO.builder()
                .compilerVersion(CompilerVersion.valueOf(submissionDTO.getCompilerVersion()))
                .language(com.example.zero2dev.storage.Language.valueOf(language.getName()))
                .timeLimit(problem.getTimeLimit())
                .sourceCode(submissionDTO.getSourceCode())
                .testCases(testCaseReaderService.getTestCases(problem.getId()))
                .build();
        if (compileCodeDTO.getTestCases().size()==0){
            throw new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION);
        }
        ListCompileCodeResponse compileCodeResponse = this.compileCodeService.compileCode(compileCodeDTO);
        int totalTest = compileCodeResponse.getTotalTests();
        boolean isAllTestPassed = false;
        submission.setMemoryUsed(compileCodeResponse.getTotalMemoryUsed());
        submission.setProblem(problem);
        submission.setContest(this.getContest(submissionDTO.getContestId()));
        submission.setUser(this.getUser(submissionDTO.getUserId()));
        submission.setLanguage(language);
        submission.setExecutionTime(compileCodeResponse.getTotalExecutionTime());
        int failedAt = 0;
        String message = "";
        if (compileCodeResponse.isAllTestsPassed() && compileCodeResponse.getFailedAt()<=0){
            submission.setStatus(SubmissionStatus.ACCEPTED);
            message = MESSAGE.ACCEPTED_STATUS;
            isAllTestPassed = true;
        } else {
            if (!compileCodeResponse.isAllTestsPassed() && compileCodeResponse.getFailedAt() > 0) {
                submission.setStatus(SubmissionStatus.WRONG_ANSWER);
                failedAt = (int) compileCodeResponse.getFailedAt();
                message = MESSAGE.WRONG_ANSWER;
            }
            List<CompileCodeResponse> list = compileCodeResponse.getCompileCodeResponses();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isTimeLimit()||"time limit exceeded".equalsIgnoreCase(list.get(i).getMessage())) {
                    submission.setStatus(SubmissionStatus.TIME_LIMIT_EXCEEDED);
                    failedAt = i + 1;
                    message = MESSAGE.TIME_LIMIT_EXCEEDED;
                    break;
                }
                if (this.isCompileError(list.get(i).getMessage())) {
                    submission.setStatus(SubmissionStatus.COMPILE_ERROR);
                    failedAt = i + 1;
                    message = MESSAGE.COMPILE_ERROR;
                    break;
                }
                if (!list.get(i).isPassed()) {
                    submission.setStatus(SubmissionStatus.WRONG_ANSWER);
                    failedAt = i + 1;
                    message = MESSAGE.WRONG_ANSWER;
                    break;
                }
            }
        }
        submission.setMessage(message);
        submission.setFailedAt((long)failedAt);
        submission.setTotalTest((long)totalTest);
        SubmissionResponse response = SubmissionResponse.exchangeEntity(this.submissionRepository.save(submission));
        if (submission.getStatus().equals(SubmissionStatus.ACCEPTED)) {
            this.codeStorageService.createCodeStorage(this.parseDTO(
                    submission, submissionDTO.getSourceCode()));
            this.problemService.incrementAcceptedSubmissionCount(submission.getProblem().getId(), SubmissionStatus.ACCEPTED);
            this.userService.updateTotalSolved(submission.getUser().getId(), SubmissionStatus.ACCEPTED);
        }
        this.problemService.increaseValue(submission.getProblem());
        response.setFailedAt((long)failedAt);
        response.setMessage(message);
        response.setLanguage(language);
        response.setAllTestPassed(isAllTestPassed);
        response.setTotalTest((long)totalTest);
        return response;
    }
    @Override
    public List<SubmissionResponse> getSubmissionByUserId(Long userId) {
        return Optional.ofNullable(this.submissionRepository.findByUserId(userId))
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(submission -> {
                            SubmissionResponse submissionResponse = SubmissionResponse.exchangeEntity(submission);
                            submissionResponse.setSourceCode(this.getSourceCodeAC(submission));
                            return submissionResponse;
                        })
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }

    @Override
    public List<SubmissionResponse> getSubmissionByProblemId(Long problemId) {
        return Optional.ofNullable(this.submissionRepository.findByProblemId(problemId))
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(SubmissionResponse::exchangeEntity)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }

    @Override
    public List<SubmissionResponse> getSubmissionByLanguageName(String name) {
        return Optional.ofNullable(this.submissionRepository.findByLanguageName(name))
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(SubmissionResponse::exchangeEntity)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }

    @Override
    public List<SubmissionResponse> getSubmissionByStatus(String status) {
        return Optional.ofNullable(this.submissionRepository.findByStatus(SubmissionStatus.valueOf(status)))
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(SubmissionResponse::exchangeEntity)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }

    @Override
    public List<SubmissionResponse> getSubmissionLowestMemoryUsed() {
        return Optional.ofNullable(this.submissionRepository.findSubmissionsWithLowestMemoryUsed())
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(SubmissionResponse::exchangeEntity)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
    @Override
    public List<SubmissionResponse> getSubmissionByLowestExecutionTime() {
        return Optional.ofNullable(this.submissionRepository.findSubmissionsWithLowestExecutionTime())
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(SubmissionResponse::exchangeEntity)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
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
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
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
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }

    @Override
    public SubmissionResponse getSubmissionByUserIdAndProblemId(Long userId, Long problemId) {
        Submission submission = this.getLatestSubmission(userId, problemId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
        String sourceCode = this.getSourceCodeAC(submission);
        SubmissionResponse response = SubmissionResponse.exchangeEntity(submission);
        response.setSourceCode(sourceCode);
        return response;
    }

    private String getSourceCodeAC(Submission submission){
        return this.codeStorageService.findCodeStorageAccepted(
                submission.getUser().getId(),
                submission.getProblem().getId(),
                SubmissionStatus.ACCEPTED).getSourceCode();
    }
    public Submission getSubmissionById(Long id){
        return this.submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
    public Pair<Pair<User, Problem>, Language> checkValidSubmission(SubmissionDTO submissionDTO){
        User user = this.userRepository.findById(submissionDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
        if (!user.getIsActive()){
            throw new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION);
        }
        Problem problem = this.problemRepository.findById(submissionDTO.getProblemId())
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
        if (!problem.getIsActive()){
            throw new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION);
        }
        Language language = Optional.of(this.languageRepository.findByVersion(submissionDTO.getCompilerVersion()))
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
        if (!language.getIsActive()){
            throw new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION);
        }
        return Pair.of(Pair.of(user, problem), language);
    }
    public Language getLanguage(Long id){
        return this.languageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
    public User getUser(Long id){
        return this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
    public Problem getProblem(Long id){
        return this.problemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
    private Contest getContest(Long id){
        return this.contestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
    private CodeStorageDTO parseDTO(Submission submission, String sourceCode){
        return CodeStorageDTO.builder()
                .problemId(submission.getProblem().getId())
                .userId(submission.getUser().getId())
                .sourceCode(sourceCode)
                .build();
    }
    private Language getLanguageByName(String name){
        return Optional.of(languageRepository.findByName(name))
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
    private Submission exchangeEntity(Submission submission, SubmissionDTO submissionDTO){
        submission.setContest(this.getContest(submissionDTO.getContestId()));
        submission.setStatus(submission.getStatus());
        submission.setLanguage(this.getLanguageByCompilerVersion(submissionDTO.getCompilerVersion()));
        submission.setProblem(this.getProblem(submissionDTO.getProblemId()));
        submission.setUser(this.getUser(submissionDTO.getUserId()));
        return submission;
    }
    private void checkValidProblemTimeLimit(Problem problem){
        if (problem.getTimeLimit()<10){
            throw new ResourceNotFoundException(MESSAGE.GENERAL_ERROR);
        }
    }
    private Language getLanguageByCompilerVersion(String compilerVersion){
        return Optional.of(this.languageRepository.findByVersion(compilerVersion))
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
    private boolean isCompileError(String message) {
        return message != null && (
                message.contains("IndentationError") ||
                        message.contains("SyntaxError") ||
                        message.contains("compile error") ||
                        message.contains("error")
        );
    }
    public Optional<Submission> getLatestSubmission(Long userId, Long problemId) {
        return submissionRepository
                .findFirstByUser_IdAndProblem_IdOrderByCreatedAtDesc(userId, problemId);
    }
    public Submission findOrThrow(Long userId, Long problemId) {
        return submissionRepository
                .findFirstByUser_IdAndProblem_IdOrderByCreatedAtDesc(userId, problemId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
}
