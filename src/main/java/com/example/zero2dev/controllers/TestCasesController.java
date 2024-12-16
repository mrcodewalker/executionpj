package com.example.zero2dev.controllers;

import com.example.zero2dev.dtos.TestCasesDTO;
import com.example.zero2dev.services.TestCasesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test_cases")
@RequiredArgsConstructor
public class TestCasesController {
    private final TestCasesService testCasesService;
    @PostMapping("/create")
    public ResponseEntity<?> createTestCase(
            @RequestBody TestCasesDTO testCasesDTO){
        return ResponseEntity.ok(this.testCasesService.createTestCase(testCasesDTO));
    }
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateTestCase(
            @PathVariable("id") Long id,
            @RequestBody TestCasesDTO testCasesDTO){
        return ResponseEntity.ok(this.testCasesService.updateTestCase(id, testCasesDTO));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(
            @PathVariable("id") Long id){
        return ResponseEntity.ok(this.testCasesService.deleteByTestCaseId(id));
    }
    @DeleteMapping("/delete/by_problem/{id}")
    public ResponseEntity<?> deleteByProblemId(
            @PathVariable("id") Long id){
        return ResponseEntity.ok(this.testCasesService.deleteAllTestCase(id));
    }
    @GetMapping("/collect/by/problem/{id}")
    public ResponseEntity<?> collectByProblemId(
            @PathVariable("id") Long id){
        return ResponseEntity.ok(this.testCasesService.getListTestCaseByProblemId(id));
    }
    @GetMapping("/collect/detail/{id}_{orderId}")
    public ResponseEntity<?> collectByProblemIdAndOrderId(
            @PathVariable("id") Long id,
            @PathVariable("orderId") Long orderId){
        return ResponseEntity.ok(this.testCasesService.getTestCaseByProblemIdAndOrder(id, orderId));
    }
    @PostMapping("/collect/all/{id}")
    public ResponseEntity<?> collectAllTestCase(@PathVariable("id") Long id){
        return ResponseEntity.ok(this.testCasesService.getAllTestCase(id));
    }
    @PostMapping("/encode/all/{id}")
    public ResponseEntity<?> encodeData(@PathVariable("id") Long id){
        this.testCasesService.encodeAllTestCase(id);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
}
