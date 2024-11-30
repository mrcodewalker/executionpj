package com.example.zero2dev.controllers;

import com.example.zero2dev.dtos.ContestParticipantDTO;
import com.example.zero2dev.models.ContestParticipantKey;
import com.example.zero2dev.services.ContestParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/participant")
public class ContestParticipantController {
    private final ContestParticipantService contestParticipantService;
    @PostMapping("/create")
    public ResponseEntity<?> joinContest(
            @RequestBody ContestParticipantDTO contestParticipantDTO){
        return ResponseEntity.ok(this.contestParticipantService.joinContest(contestParticipantDTO));
    }
    @GetMapping("/filter/contest/{id}")
    public ResponseEntity<?> getListUserByContestId(
            @PathVariable("id") Long contestId){
        return ResponseEntity.ok(this.contestParticipantService
                .getListUserJoined(contestId));
    }
    @GetMapping("/filter/user/{id}")
    public ResponseEntity<?> getListContestByUserId(
            @PathVariable("id") Long userId){
        return ResponseEntity.ok(this.contestParticipantService.getListContestUserJoined(userId));
    }
    @DeleteMapping("/delete/record")
    public ResponseEntity<?> deleteByKey(
            @RequestBody ContestParticipantKey contestParticipantKey){
        return ResponseEntity.ok(
                this.contestParticipantService.deleteByGroupKey(contestParticipantKey));
    }
    @PutMapping("/update/record")
    public ResponseEntity<?> updateRecord(
            @RequestBody ContestParticipantKey contestParticipantKey,
            @RequestParam("problemId") Long problemId){
        return ResponseEntity.ok(this.contestParticipantService
                .updateTotalScore(contestParticipantKey, problemId));
    }
    @GetMapping("/filter/key")
    public ResponseEntity<?> filterByKey(
            @RequestBody ContestParticipantKey contestParticipantKey){
        return ResponseEntity.ok(this.contestParticipantService.getDetailByGroupKey(contestParticipantKey));
    }
}
