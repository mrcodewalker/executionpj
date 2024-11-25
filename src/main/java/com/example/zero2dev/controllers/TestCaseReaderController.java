package com.example.zero2dev.controllers;

import com.example.zero2dev.dtos.TestCaseReaderDTO;
import com.example.zero2dev.services.TestCaseReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/test_case")
@RequiredArgsConstructor
public class TestCaseReaderController {
    private final TestCaseReaderService testCaseReaderService;
    @PostMapping("/create")
    public ResponseEntity<?> createTestCase(
            @RequestBody TestCaseReaderDTO testCaseReaderDTO
            ){
        return ResponseEntity.ok(
                testCaseReaderService.createTestCase(testCaseReaderDTO));
    }
    @PostMapping("/problem")
    public ResponseEntity<?> getListTestCases(
            @RequestParam("id") Long id){
        return ResponseEntity.ok(this.testCaseReaderService.getListTestCase(id));
    }
    @GetMapping("/position/{id}")
    public ResponseEntity<?> getTestCase(
            @PathVariable("id") Long id){
        return ResponseEntity.ok(this.testCaseReaderService.getTestCaseById(id));
    }
    @PostMapping("/update")
    public ResponseEntity<?> updateTestCase(
            @RequestParam("id") Long id,
            @RequestBody TestCaseReaderDTO testCaseReaderDTO){
        return
                ResponseEntity.ok(this.testCaseReaderService.updateTestCase(id, testCaseReaderDTO));
    }
    @PostMapping("/delete")
    public ResponseEntity<?> updateTestCase(@RequestParam("id") Long id){
        return
                ResponseEntity.ok(this.testCaseReaderService.deleteTestCase(id));
    }
}
