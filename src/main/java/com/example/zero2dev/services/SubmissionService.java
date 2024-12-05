package com.example.zero2dev.services;

import com.example.zero2dev.controllers.SubmissionController;
import com.example.zero2dev.dtos.CodeStorageDTO;
import com.example.zero2dev.dtos.CompileCodeDTO;
import com.example.zero2dev.dtos.SubmissionDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.exceptions.ValueNotValidException;
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
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
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
    private final ContestParticipantService contestParticipantService;
    @Override
    public SubmissionResponse createSubmission(SubmissionDTO submissionDTO) {
        SecurityService.validateUserIdExceptAdmin(submissionDTO.getUserId());
        if (!contestParticipantService.joinedContest(submissionDTO.getContestId(), submissionDTO.getUserId())) {
            throw new ValueNotValidException(MESSAGE.GENERAL_ERROR);
        }

        Optional<Submission> existingSubmission = getLatestSubmissionValue(submissionDTO.getUserId(), submissionDTO.getProblemId());
        Submission submission = prepareSubmission(submissionDTO, existingSubmission);

        CompileCodeDTO compileCodeDTO = buildCompileCodeDTO(submissionDTO, submission);
        if (compileCodeDTO.getTestCases().isEmpty()) {
            throw new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION);
        }

        ListCompileCodeResponse compileCodeResponse = compileCodeService.compileCode(compileCodeDTO);
        String detailMessage = processCompileResults(submission, compileCodeResponse);

        SubmissionResponse response = SubmissionResponse.exchangeEntity(submissionRepository.save(submission));

        if (submission.getStatus().equals(SubmissionStatus.ACCEPTED)) {
            codeStorageService.createCodeStorage(parseDTO(submission, submissionDTO.getSourceCode()));
            response.setSourceCode(compileCodeDTO.getSourceCode());
        }

        enrichResponse(response, submission, compileCodeResponse, detailMessage);
        return response;
    }
    private Submission notExistsInDataBase(SubmissionDTO submissionDTO){
        Pair<Pair<User, Problem>, Pair<Language, Contest>> data = this.checkValidSubmission(submissionDTO);
        Submission submission = new Submission();
        submission.setContest(data.getSecond().getSecond());
        submission.setUser(data.getFirst().getFirst());
        submission.setLanguage(data.getSecond().getFirst());
        submission.setProblem(data.getFirst().getSecond());
        return submission;
    }
    @Override
    public List<SubmissionResponse> getSubmissionByUserId(Long userId) {
        SecurityService.validateUserIdExceptAdmin(userId);
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
        SecurityService.validateUserIdExceptAdmin(userId);
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
        SecurityService.validateUserIdExceptAdmin(userId);
        Object[] data = this.getLatestSubmission(userId, problemId).get(0);
        SubmissionResponse response = SubmissionResponse
                .builder()
                .userId(userId)
                .problem(this.getProblem(problemId))
                .sourceCode((String)data[2])
                .executionTime((Long)data[3])
                .memoryUsed((Long)data[4])
                .message((String)data[5])
                .failedAt((Long)data[6])
                .totalTest((Long)data[7])
                .status(SubmissionStatus.valueOf((String)data[8]))
                .languageName((String)data[9])
                .compilerVersion((String)data[10])
                .id((Long)data[11])
                .contestId((Long)data[12])
                .build();
        return response;
    }

    @Override
    public List<ContestRankingResponse> getRankingByContestId(Long contestId) {
        List<Object[]> data = this.submissionRepository.getContestRankingByContestId(contestId, SubmissionStatus.ACCEPTED.toString());
        System.out.println(SubmissionStatus.ACCEPTED+"");
        List<ContestRankingResponse> responses = new ArrayList<>();
        Long cnt = 1L;
        for (Object[] clone: data){
            ContestRankingResponse contestRankingResponse = this.mappingFromObject(clone);
            contestRankingResponse.setRank(cnt++);
            responses.add(contestRankingResponse);
        }
        return responses;
    }
    private ContestRankingResponse mappingFromObject(Object[] index){
        return ContestRankingResponse.builder()
                .contestId((long)index[0])
                .userId((long)index[1])
                .userName((String)index[2])
                .totalScore((index[3] != null) ? ((BigDecimal)index[3]).toBigInteger().longValue() : 0L)
                .totalExecutionTime((index[4]!=null) ? ((BigDecimal)index[4]).toBigInteger().longValue() : 0L)
                .totalMemoryUsed((index[5]!=null) ? ((BigDecimal)index[5]).toBigInteger().longValue() : 0L)
                .build();
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
    @Transactional(readOnly = true)
    public Pair<Pair<User, Problem>, Pair<Language, Contest>> checkValidSubmission(SubmissionDTO submissionDTO) {
        List<Object[]> results = submissionRepository.findSubmissionValidationData(
                submissionDTO.getUserId(),
                submissionDTO.getProblemId(),
                submissionDTO.getContestId(),
                submissionDTO.getCompilerVersion()
        );

        if (results.isEmpty()) {
            throw new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION);
        }

        Object[] data = results.get(0);
        User user = (User) data[0];
        Problem problem = (Problem) data[1];
        Language language = (Language) data[2];
        Contest contest = (Contest) data[3];

        if (!user.getIsActive() || !problem.getIsActive() || !language.getIsActive()) {
            throw new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION);
        }

        return Pair.of(Pair.of(user, problem), Pair.of(language, contest));
    }
//    public Pair<Pair<User, Problem>, Pair<Language,Contest>> checkValidSubmission(SubmissionDTO submissionDTO){
//        User user = this.userRepository.findById(submissionDTO.getUserId())
//                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
//        if (!user.getIsActive()){
//            throw new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION);
//        }
//        Problem problem = this.problemRepository.findById(submissionDTO.getProblemId())
//                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
//        if (!problem.getIsActive()){
//            throw new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION);
//        }
//        Language language = Optional.of(this.languageRepository.findByVersion(submissionDTO.getCompilerVersion()))
//                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
//        if (!language.getIsActive()){
//            throw new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION);
//        }
//        Contest contest = this.contestRepository.findById(submissionDTO.getContestId())
//                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
//        return Pair.of(Pair.of(user, problem), Pair.of(language, contest));
//    }
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
    public List<Object[]> getLatestSubmission(Long userId, Long problemId) {
        return submissionRepository
                .getDetailSubmissionByUserIdAndProblemId(userId, problemId, "ACCEPTED");
    }
    public Optional<Submission> getLatestSubmissionValue(Long userId, Long problemId) {
        return Optional.ofNullable(submissionRepository
                .findFirstByUser_IdAndProblem_IdOrderByCreatedAtDesc(userId, problemId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION)));
    }
    public Submission findOrThrow(Long userId, Long problemId) {
        return submissionRepository
                .findFirstByUser_IdAndProblem_IdOrderByCreatedAtDesc(userId, problemId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
    private Submission prepareSubmission(SubmissionDTO submissionDTO, Optional<Submission> existingSubmission) {
        Submission submission = mapper.toEntity(submissionDTO);
        if (existingSubmission.isPresent()) {
            Submission existing = existingSubmission.get();
            submission.setId(existing.getId());
            submission.setCreatedAt(existing.getCreatedAt());
            submission.setLanguage(this.getLanguageByCompilerVersion(submissionDTO.getCompilerVersion()));
            submission.setUser(existing.getUser());
            submission.setProblem(existing.getProblem());
            submission.setContest(existing.getContest());
        } else {
            Submission newSubmission = notExistsInDataBase(submissionDTO);
            submission.setProblem(newSubmission.getProblem());
            submission.setLanguage(newSubmission.getLanguage());
            submission.setUser(newSubmission.getUser());
            submission.setContest(newSubmission.getContest());
        }
        return submission;
    }

    private CompileCodeDTO buildCompileCodeDTO(SubmissionDTO submissionDTO, Submission submission) {
        return CompileCodeDTO.builder()
                .compilerVersion(CompilerVersion.valueOf(submission.getLanguage().getVersion()))
                .language(com.example.zero2dev.storage.Language.valueOf(submission.getLanguage().getName()))
                .timeLimit(submission.getProblem().getTimeLimit())
                .sourceCode(submissionDTO.getSourceCode())
                .testCases(this.testCaseReaderService.getTestCases(
                        submissionDTO.getProblemId()
                        ,submission.getProblem().getTestCases()))
                .build();
    }

    private String processCompileResults(Submission submission, ListCompileCodeResponse compileCodeResponse) {
        submission.setMemoryUsed(compileCodeResponse.getTotalMemoryUsed());
        submission.setExecutionTime(compileCodeResponse.getTotalExecutionTime());
        int failedAt = 0;
        String message = "";
        String detailMessage = "Passed";

        if (compileCodeResponse.isAllTestsPassed() && compileCodeResponse.getFailedAt() <= 0) {
            submission.setStatus(SubmissionStatus.ACCEPTED);
            message = MESSAGE.ACCEPTED_STATUS;
        } else {
            for (int i = 0; i < compileCodeResponse.getCompileCodeResponses().size(); i++) {
                CompileCodeResponse result = compileCodeResponse.getCompileCodeResponses().get(i);
                if (result.isTimeLimit() || "time limit exceeded".equalsIgnoreCase(result.getMessage())) {
                    submission.setStatus(SubmissionStatus.TIME_LIMIT_EXCEEDED);
                    failedAt = i + 1;
                    message = MESSAGE.TIME_LIMIT_EXCEEDED;
                    detailMessage = result.getMessage();
                    break;
                }
                if (isCompileError(result.getMessage())) {
                    submission.setStatus(SubmissionStatus.COMPILE_ERROR);
                    failedAt = i + 1;
                    message = MESSAGE.COMPILE_ERROR;
                    detailMessage = result.getMessage();
                    break;
                }
                if (!result.isPassed()) {
                    submission.setStatus(SubmissionStatus.WRONG_ANSWER);
                    failedAt = i + 1;
                    message = MESSAGE.WRONG_ANSWER;
                    detailMessage = result.getMessage();
                    break;
                }
            }
        }
        submission.setMessage(message);
        submission.setFailedAt((long) failedAt);
        submission.setTotalTest((long) compileCodeResponse.getTotalTests());
        return detailMessage;
    }

    private void enrichResponse(SubmissionResponse response, Submission submission, ListCompileCodeResponse compileCodeResponse, String detailMessage) {
        response.setFailedAt(submission.getFailedAt());
        response.setMessage(submission.getMessage());
        response.setLanguageName(submission.getLanguage().getName());
        response.setCompilerVersion(submission.getLanguage().getVersion());
        response.setAllTestPassed(compileCodeResponse.isAllTestsPassed());
        response.setTotalTest(submission.getTotalTest());
        response.setDetailMessage(detailMessage);
    }
}
