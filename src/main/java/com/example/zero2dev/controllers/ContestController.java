package com.example.zero2dev.controllers;

import com.example.zero2dev.dtos.ContestDTO;
import com.example.zero2dev.services.ContestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contest")
public class ContestController {
    private final ContestService contestService;
    @PostMapping("/create")
    public ResponseEntity<?> createContest(
            @RequestBody ContestDTO contestDTO){
        return ResponseEntity.ok(this.contestService.createContest(contestDTO));
    }
    @GetMapping("/get")
    public ResponseEntity<?> getContestById(
            @RequestParam("id") Long id){
        return ResponseEntity.ok(this.contestService.getContestById(id));
    }
    @GetMapping("/filter/all")
    public ResponseEntity<?> getAllContest(){
        return ResponseEntity.ok(this.contestService.filterAll());
    }
    @PostMapping("/available/contest")
    public ResponseEntity<?> getAvailableContest(){
        return ResponseEntity.ok(this.contestService.listContestValid());
    }
    @PostMapping("/delete")
    public ResponseEntity<?> deleteContestById(
            @RequestParam("id") Long id){
        return ResponseEntity.ok(this.contestService.deleteContestById(id));
    }
    @PostMapping("/update/contest")
    public ResponseEntity<?> updateContest(
            @RequestParam("id") Long id,
            @RequestBody ContestDTO contestDTO){
        return ResponseEntity.ok(this.contestService.updateContest(id, contestDTO));
    }
}
