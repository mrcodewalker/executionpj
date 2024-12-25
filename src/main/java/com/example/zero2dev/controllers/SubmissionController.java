package com.example.zero2dev.controllers;

import com.example.zero2dev.dtos.SubmissionDTO;
import com.example.zero2dev.models.Submission;
import com.example.zero2dev.services.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/submission")
@RequiredArgsConstructor
public class SubmissionController {
    private final SubmissionService submissionService;
    @PostMapping("/create")
    public ResponseEntity<?> createSubmission(
            @RequestBody SubmissionDTO submissionDTO){
        return ResponseEntity.ok(this.submissionService.createSubmission(submissionDTO));
    }
    @GetMapping("/ranking/contest/{id}")
    public ResponseEntity<?> filterByContestId(
            @PathVariable("id") Long id){
        return ResponseEntity.ok(this.submissionService.getRankingByContestId(id));
    }
    @GetMapping("/problem/solved/{id}")
    public ResponseEntity<?> filterProblemSolvedByContestId(
            @PathVariable("id") Long id){
        return ResponseEntity.ok(this.submissionService.getProblemsSolved(id));
    }
    @GetMapping("/collect/user/{id}")
    public ResponseEntity<?> collectSubmissionByUsersId(
            @PathVariable("id") Long id){
        return ResponseEntity.ok(this.submissionService.getSubmissionByUserId(id));
    }
    @GetMapping("/filter/submit")
    public ResponseEntity<?> filterSubmission(
            @RequestParam("userId") Long userId,
            @RequestParam("problemId") Long problemId){
        return ResponseEntity.ok(this.submissionService.getSubmissionByUserIdAndProblemId(userId, problemId));
    }
    @GetMapping("/collect/problem/{id}")
    public ResponseEntity<?> collectSubmissionByProblemId(
            @PathVariable("id") Long id){
        return ResponseEntity.ok(this.submissionService.getSubmissionByProblemId(id));
    }
    @GetMapping("/collect/language/{name}")
    public ResponseEntity<?> collectSubmissionByLanguageName(
            @PathVariable("name") String name){
        return ResponseEntity.ok(this.submissionService.getSubmissionByLanguageName(name));
    }
    @GetMapping("/collect/status/{status}")
    public ResponseEntity<?> collectSubmissionByStatus(
            @PathVariable("status") String status){
        return ResponseEntity.ok(this.submissionService.getSubmissionByStatus(status));
    }
    @GetMapping("/collect/memory/lowest")
    public ResponseEntity<?> collectSubmissionByMemoryUsed(){
        return ResponseEntity.ok(this.submissionService.getSubmissionLowestMemoryUsed());
    }
    @GetMapping("/collect/exec_time/lowest")
    public ResponseEntity<?> collectSubmissionByExecutionTimeLowest(){
        return ResponseEntity.ok(this.submissionService.getSubmissionByLowestExecutionTime());
    }
    @DeleteMapping("/delete/submission/{id}")
    public ResponseEntity<?> deleteBySubmissionId(
            @PathVariable("id") Long id){
        return ResponseEntity.ok(this.submissionService.deleteSubmissionById(id));
    }
    @DeleteMapping("/delete/user/{id}")
    public ResponseEntity<?> deleteByUserId(
            @PathVariable("id") Long id){
        return ResponseEntity.ok(this.submissionService.deleteSubmissionByUserId(id));
    }
    @DeleteMapping("/delete/problem/{id}")
    public ResponseEntity<?> deleteByProblemId(
            @PathVariable("id") Long id){
        return ResponseEntity.ok(this.submissionService.deleteSubmissionByProblemId(id));
    }
}
