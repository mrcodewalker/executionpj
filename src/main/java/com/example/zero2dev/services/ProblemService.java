package com.example.zero2dev.services;

import com.example.zero2dev.dtos.ProblemDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.interfaces.IProblemService;
import com.example.zero2dev.mapper.ProblemMapper;
import com.example.zero2dev.models.Category;
import com.example.zero2dev.models.Problem;
import com.example.zero2dev.repositories.CategoryRepository;
import com.example.zero2dev.repositories.ProblemRepository;
import com.example.zero2dev.responses.ProblemResponse;
import com.example.zero2dev.storage.Difficulty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProblemService implements IProblemService {
    private final ProblemRepository problemRepository;
    private final CategoryRepository categoryRepository;
    private final ProblemMapper problemMapper;

    @Override
    public ProblemResponse createProblem(ProblemDTO problemDTO) {
        Category category = this.findCategoryById(problemDTO.getCategoryId());
        Problem problem = ProblemDTO.createFromEntity(category, problemDTO);

        Problem savedProblem = problemRepository.save(problem);
        ProblemResponse response = problemMapper.toResponse(savedProblem);
        response.setCategoryId(savedProblem.getCategory().getId());
        return response;
    }

    @Override
    public ProblemResponse updateProblem(Long id, ProblemDTO problemDTO) {
        Problem problem = this.findProblemById(id);
        Category category = this.findCategoryById(problemDTO.getCategoryId());

        problemMapper.updateProblemFromDto(problemDTO, problem);

        Problem updatedProblem = problemRepository.save(problem);
        return problemMapper.toResponse(updatedProblem);
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
        return problemMapper.toResponse(problem);
    }

    @Override
    public Page<ProblemResponse> searchProblems(String title, Difficulty difficult,
                                           Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Problem> problemPage = problemRepository.searchProblems(title, difficult,
                categoryId, pageable);
        return problemPage.map(problemMapper::toResponse);
    }

    @Override
    @Transactional
    public void incrementSubmissionCount(Long problemId) {
        Problem problem = this.findProblemById(problemId);
        problem.setTotalSubmission(problem.getTotalSubmission() + 1);
        problemRepository.save(problem);
    }

    @Override
    @Transactional
    public void incrementAcceptedSubmissionCount(Long problemId) {
        Problem problem = this.findProblemById(problemId);
        problem.setAcceptedSubmission(problem.getAcceptedSubmission() + 1);
        problemRepository.save(problem);
    }
    public Problem findProblemById(Long id){
        return problemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));
    }
    public Category findCategoryById(Long id){
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }
}
