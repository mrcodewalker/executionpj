package com.example.zero2dev.services;

import com.example.zero2dev.controllers.SubmissionController;
import com.example.zero2dev.dtos.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final ContestParticipantService contestParticipantService;
    private final TestCasesService testCasesService;
    public CertificateResponse getCertificateResponse(Long contestId){
        User user = SecurityService.getUserIdFromSecurityContext();
        if (user==null){
            throw new ResourceNotFoundException(MESSAGE.IP_BLACKLISTED);
        }
        List<Object[]> results = submissionRepository.findContestStats(user.getId(), contestId);
        if (!results.isEmpty()) {
            for (Object[] row : results) {
                Long languageCount = (Long) row[0];
                String languageName = (String) row[1];
                String languageVersion = (String) row[2];
                String fullName = (String) row[3];
                Long totalSolved = (Long) row[4];
                boolean completedAllProblems = row[5] != null && ((Integer) row[5] == 1);                String contestTitle = (String) row[6];
                Long rankInContest = (Long) row[7];
                Long total = (Long) row[8];
                return CertificateResponse.builder()
                        .completedAllProblems(completedAllProblems)
                        .languageCount(languageCount)
                        .languageVersion(languageVersion)
                        .languageName(languageName)
                        .fullName(fullName)
                        .totalSolved(totalSolved)
                        .contestTitle(contestTitle)
                        .rankInContest(rankInContest)
                        .total(total)
                        .build();
            }
        }
        return null;
    }
    @Override
    public SubmissionResponse createSubmission(SubmissionDTO submissionDTO) {
        Long userId = SecurityService.getUserIdByToken();
        SecurityService.validateUserIdExceptAdmin(userId);
        if (!contestParticipantService.joinedContest(submissionDTO.getContestId(),userId)) {
            throw new ValueNotValidException(MESSAGE.GENERAL_ERROR);
        }

        Optional<Submission> existingSubmission = getLatestSubmissionValue(userId, submissionDTO.getProblemId(), submissionDTO.getContestId());

        Submission submission = prepareSubmission(submissionDTO, existingSubmission);
        CompileCodeDTO compileCodeDTO = buildCompileCodeDTO(submissionDTO, submission);
        if (compileCodeDTO.getTestCases().isEmpty()) {
            throw new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION);
        }

        ListCompileCodeResponse compileCodeResponse = compileCodeService.compileCode(compileCodeDTO);
        String detailMessage = processCompileResults(submission, compileCodeResponse);
        SubmissionResponse response = SubmissionResponse.exchangeEntity(submissionRepository.save(submission));
        response.setCompileCodeResponses(compileCodeResponse.getCompileCodeResponses());
        if (submission.getStatus().equals(SubmissionStatus.ACCEPTED)) {
            codeStorageService.createCodeStorage(parseDTO(submission, submissionDTO.getSourceCode()));
            response.setSourceCode(compileCodeDTO.getSourceCode());
        }

        enrichResponse(response, submission, compileCodeResponse, detailMessage);
        return response;
    }
    private MappingDataSubmission notExistsInDataBase(SubmissionDTO submissionDTO){
        System.out.println("notExistsInDataBase c1");
        return this.checkValidSubmission(submissionDTO);
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
    public Page<UserRankingDTO> getUserRankings(int page, int size) {
        long total = submissionRepository.countUserRankings();

        int offset = page * size;

        List<Object[]> results = submissionRepository.findUserRankings(size, offset);

        List<UserRankingDTO> dtos = results.stream()
                .map(row -> UserRankingDTO.builder()
                        .ranking(((Number) row[0]).longValue())
                        .userId(((Number) row[1]).longValue())
                        .username((String) row[2])
                        .avatarUrl((String) row[3])
                        .totalExecutionTime(((Number) row[4]).longValue())
                        .totalMemoryUsed(((Number) row[5]).longValue())
                        .totalPoints(((Number) row[6]).longValue())
                        .build())
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, PageRequest.of(page, size), total);
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
    public ProblemSolvedResponse getProblemsSolved(Long contestId){
        if (!this.contestRepository.existsById(contestId)){
            throw new ResourceNotFoundException(MESSAGE.GENERAL_ERROR);
        }
        User user = SecurityService.getUserIdFromSecurityContext();
        if (user==null){
            throw new ResourceNotFoundException(MESSAGE.GENERAL_ERROR);
        }
        List<Object[]> list = this.submissionRepository.getProblemSolved(user.getId(), contestId);
        List<SolvedResponse> responses = new ArrayList<>();
        for (int i=0;i<list.size();i++){
            Object[] clone = list.get(i);
            responses.add(SolvedResponse.builder()
                    .submissionId((Long)clone[0])
                    .problemId((Long)clone[2])
                    .sourceCode((String)clone[3])
                    .status((String)clone[5])
                    .executionTime((Long)clone[6])
                    .memoryUsed((Long)clone[7])
                    .contestId((Long)clone[8])
                    .message((String)clone[9])
                    .failedAt((Long)clone[10])
                    .totalTest((Long)clone[11])
                    .languageName((String)clone[12])
                    .compilerVersion((String)clone[13])
                    .build());
        }
        return ProblemSolvedResponse.builder()
                .responseList(responses)
                .build();
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
                .avatarUrl((((String) index[6]).length()>0) ? (String)index[6] : "https://imgur.com/dahLgEU")
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
    public MappingDataSubmission checkValidSubmission(SubmissionDTO submissionDTO) {
        System.out.println("checkValidSubmission c1");
        List<Object[]> results = submissionRepository.findSubmissionValidationData(
                SecurityService.getUserIdByToken(),
                submissionDTO.getProblemId(),
                submissionDTO.getContestId(),
                submissionDTO.getCompilerVersion()
        );
        System.out.println("checkValidSubmission c2");
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

        return MappingDataSubmission.builder()
                .user(user)
                .problem(problem)
                .contest(contest)
                .language(language)
                .build();
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
    public Optional<Submission> getLatestSubmissionValue(Long userId, Long problemId, Long contestId) {
        return submissionRepository
                .findFirstByUser_IdAndProblem_IdAndContest_IdOrderByCreatedAtDesc(userId, problemId, contestId);
    }
    public Submission findOrThrow(Long userId, Long problemId, Long contestId) {
        return submissionRepository
                .findFirstByUser_IdAndProblem_IdAndContest_IdOrderByCreatedAtDesc(userId, problemId, contestId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
    private Submission prepareSubmission(SubmissionDTO submissionDTO, Optional<Submission> existingSubmission) {
        Submission submission = mapper.toEntity(submissionDTO);
        System.out.println(submission+"HAI DEP TRAI 1---2");
        if (existingSubmission.isPresent()) {
            System.out.println(submission+"HAI DEP TRAI c1");
            submission = existingSubmission.get();
            submission.setLanguage(this.getLanguageByCompilerVersion(submissionDTO.getCompilerVersion()));
        } else {
            MappingDataSubmission newSubmission = notExistsInDataBase(submissionDTO);
            newSubmission.setProblem(newSubmission.getProblem());
            submission.setProblem(newSubmission.getProblem());
            submission.setLanguage(newSubmission.getLanguage());
            submission.setUser(newSubmission.getUser());
            submission.setContest(newSubmission.getContest());
        }
        return submission;
    }

    private CompileCodeDTO buildCompileCodeDTO(SubmissionDTO submissionDTO, Submission submission) {
        return CompileCodeDTO.builder()
                .compilerVersion(CompilerVersion.valueOf(submissionDTO.getCompilerVersion()))
                .language(com.example.zero2dev.storage.Language.valueOf(submission.getLanguage().getName()))
                .timeLimit(submission.getProblem().getTimeLimit())
                .sourceCode(submissionDTO.getSourceCode())
                .testCases(
                        this.testCasesService.getTestCaseDataService(submission.getProblem().getId())
                )
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
    private TestCase toEntity(TestCasesResponse testCases){
        return TestCase.builder()
                .input(testCases.getInput())
                .expectedOutput(testCases.getOutput())
                .build();
    }
}
