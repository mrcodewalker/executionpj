package com.example.zero2dev.controllers;

import com.example.zero2dev.dtos.ContestRankingDTO;
import com.example.zero2dev.services.ContestRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ranking")
@RequiredArgsConstructor
public class ContestRankingController {
    private final ContestRankingService contestRankingService;
    @PostMapping("/create")
    public ResponseEntity<?> createContestRanking(
            @RequestBody ContestRankingDTO contestRankingDTO){
        return ResponseEntity.ok(this.contestRankingService.createContestRanking(contestRankingDTO));
    }
    @PostMapping("/update")
    public ResponseEntity<?> updateContestRanking(
            @RequestParam("id") Long id,
            @RequestBody ContestRankingDTO contestRankingDTO){
        return ResponseEntity.ok(this.contestRankingService.updateContestRanking(id, contestRankingDTO));
    }
    @GetMapping("/filter/user/{id}")
    public ResponseEntity<?> filterByUserId(
            @PathVariable("id") Long id){
        return ResponseEntity.ok(this.contestRankingService.getUserRanking(id));
    }
    @GetMapping("/filter/contest/{id}")
    public ResponseEntity<?> filterByContestId(
            @PathVariable("id") Long id){
        return ResponseEntity.ok(this.contestRankingService.getListRankingByContestId(id));
    }
    @GetMapping("/filter/info")
    public ResponseEntity<?> filterByContestId(
            @RequestParam("userId") Long userId,
            @RequestParam("contestId") Long contestId){
        return ResponseEntity.ok(this.contestRankingService.getRankingUserContest(userId, contestId));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(
            @PathVariable("id") Long id){
        return ResponseEntity.ok(this.contestRankingService.deleteUserRankingById(id));
    }
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id){
        return ResponseEntity.ok(this.contestRankingService.getById(id));
    }
    @GetMapping("/filter/all")
    public ResponseEntity<?> getAllContestRanking(){
        return ResponseEntity.ok(this.contestRankingService.getListHighestScoreByEachContest());
    }
}