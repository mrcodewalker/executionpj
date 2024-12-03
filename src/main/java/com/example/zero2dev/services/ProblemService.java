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
import com.example.zero2dev.responses.ProblemResponse;
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

import java.util.List;
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
        return this.exchangeEntity(this.findProblemById(id));
    }

    @Override
    public Page<ProblemResponse> searchProblems(String title, Difficulty difficult,
                                           Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Problem> problemPage = problemRepository.searchProblems(title, difficult,
                categoryId, pageable);
        return problemPage.map(this::exchangeEntity);
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
    public ProblemResponse exchangeEntity(Problem problem){
        ProblemResponse response = problemMapper.toResponse(problem);
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
        return response;
    }
}
