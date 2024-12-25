package com.example.zero2dev.controllers;

import com.example.zero2dev.dtos.ProblemDTO;
import com.example.zero2dev.models.Problem;
import com.example.zero2dev.responses.CustomPageResponse;
import com.example.zero2dev.responses.ProblemResponse;
import com.example.zero2dev.responses.ProblemSolvedResponse;
import com.example.zero2dev.services.ProblemService;
import com.example.zero2dev.services.SubmissionService;
import com.example.zero2dev.storage.Difficulty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/problem")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;
    private final SubmissionService submissionService;

    @PostMapping("/create")
    public ResponseEntity<?> createProblem(@RequestBody ProblemDTO problemDTO) {
        ProblemResponse problem = problemService.createProblem(problemDTO);
        return new ResponseEntity<>(problem, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProblem(
            @PathVariable Long id,
            @RequestBody ProblemDTO problemDTO) {
        ProblemResponse problem = problemService.updateProblem(id, problemDTO);
        return ResponseEntity.ok(problem);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteProblem(@PathVariable Long id) {
        problemService.deleteProblem(id);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getProblem(@PathVariable Long id) {
        ProblemResponse problem = problemService.getProblemById(id);
        return ResponseEntity.ok(problem);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProblems(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Difficulty difficult,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long contestId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        ProblemSolvedResponse response = this.submissionService.getProblemsSolved(contestId);
        ProblemSolvedResponse realResponse = this.problemService.searchProblems(response, title, difficult, contestId, categoryId, page, size);
        return ResponseEntity.ok(realResponse);
    }
}