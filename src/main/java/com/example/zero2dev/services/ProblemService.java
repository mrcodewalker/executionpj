package com.example.zero2dev.services;

import com.example.zero2dev.dtos.ProblemDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.interfaces.IProblemService;
import com.example.zero2dev.mapper.ProblemMapper;
import com.example.zero2dev.models.Category;
import com.example.zero2dev.models.Problem;
import com.example.zero2dev.repositories.CategoryRepository;
import com.example.zero2dev.repositories.ProblemRepository;
import com.example.zero2dev.repositories.SubmissionRepository;
import com.example.zero2dev.responses.CustomPageResponse;
import com.example.zero2dev.responses.ProblemResponse;
import com.example.zero2dev.responses.ProblemSolvedResponse;
import com.example.zero2dev.storage.Difficulty;
import com.example.zero2dev.storage.MESSAGE;
import com.example.zero2dev.storage.SubmissionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProblemService implements IProblemService {
    private final ProblemRepository problemRepository;
    private final CategoryRepository categoryRepository;
    private final ProblemMapper problemMapper;
    private final SubmissionRepository submissionRepository;
    private final TestCasesService testCasesService;
    private final ContestParticipantService contestParticipantService;

    @Override
    public ProblemResponse createProblem(ProblemDTO problemDTO) {
        Category category = this.findCategoryById(problemDTO.getCategoryId());
        Problem problem = ProblemDTO.createFromEntity(category, problemDTO);

        Problem savedProblem = problemRepository.save(problem);
        ProblemResponse response = this.exchangeEntity(savedProblem);
        response.setCategory(savedProblem.getCategory());
        return response;
    }

    @Override
    public ProblemResponse updateProblem(Long id, ProblemDTO problemDTO) {
        Problem problem = this.findProblemById(id);
        Category category = this.findCategoryById(problemDTO.getCategoryId());

        problemMapper.updateProblemFromDto(problemDTO, problem);

        Problem updatedProblem = problemRepository.save(problem);
        return this.exchangeEntity(updatedProblem);
    }

    @Override
    public void deleteProblem(Long id) {
        Problem problem = this.findProblemById(id);
        problem.setIsActive(false);
        problemRepository.save(problem);
    }

    @Override
    public ProblemResponse getProblemById(Long id) {
        Problem problem = this.findProblemById(id);
        ProblemResponse problemResponse = this.exchangeEntity(problem);
        problemResponse.setList(this.testCasesService.getExampleTestCase(
                Math.toIntExact(problem.getLimitTest()),id));
        return problemResponse;
    }

    @Override
    public ProblemSolvedResponse searchProblems(ProblemSolvedResponse response,
                                                String title, Difficulty difficult,
                                                Long contestId, Long categoryId, int page, int size) {
        if (!contestParticipantService.isUserJoinedContest(contestId, Objects.requireNonNull(SecurityService.getUserIdFromSecurityContext()).getId())){
            throw new ResourceNotFoundException(MESSAGE.HAVE_NOT_JOINED);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Problem> problemPage = problemRepository.searchProblems(title, difficult,
                categoryId,contestId, pageable);
        return ProblemSolvedResponse.builder()
                .responseList(response.getResponseList())
                .page(new CustomPageResponse<>(problemPage.map(this::exchangeEntity)))
                .build();
    }

    @Override
    @Transactional
    public void incrementSubmissionCount(Long problemId) {
        Problem problem = this.findProblemById(problemId);
//        problem.setTotalSubmission(problem.getTotalSubmission() + 1);
        problemRepository.save(problem);
    }
    public void increaseValue(Problem problem) {
//        problem.setTotalSubmission(problem.getTotalSubmission() + 1);
        problemRepository.save(problem);
    }

    @Override
    @Transactional
    public void incrementAcceptedSubmissionCount(Long problemId, SubmissionStatus status) {
        Problem problem = this.findProblemById(problemId);
        Long totalAccepted = this.submissionRepository.countByProblemIdAndStatus(problemId, status);
//        problem.setAcceptedSubmission(totalAccepted);
        problemRepository.save(problem);
    }

    @Override
    public Long totalAccepted(Long problemId) {
        return this.submissionRepository.countByProblemIdAndStatus(
                problemId, SubmissionStatus.ACCEPTED);
    }

    @Override
    public Long totalSubmission(Long problemId) {
        return this.submissionRepository.countSubmissionByProblemId(problemId);
    }

    public Problem findProblemById(Long id){
        return problemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
    public Category findCategoryById(Long id){
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
    public final ProblemResponse exchangeEntity(Problem problem){
        ProblemResponse response = problemMapper.toResponse(problem);
        response.setId(problem.getId());
        response.setCategory(problem.getCategory());
        List<Object[]> data = this.submissionRepository.collectProblemGraph(problem.getId());
        Long sum = 0L;
        Long totalAccepted = 0L;
        for (Object[] clone: data){
            if (clone[0].equals(SubmissionStatus.ACCEPTED)){
                totalAccepted = (Long) clone[1];
            }
            sum += (Long) clone[1];
        }
        response.setAcceptedSubmission(totalAccepted);
        response.setTotalSubmission(sum);
        response.setTag(problem.getTag());
        response.setConstraints(problem.getConstraints());
        response.setMemoryLimit(problem.getMemoryLimit());
        response.setInputFormat(problem.getInputFormat());
        response.setOutputFormat(problem.getOutputFormat());
        return response;
    }
}
