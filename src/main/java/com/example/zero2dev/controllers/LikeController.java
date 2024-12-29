package com.example.zero2dev.controllers;

import com.example.zero2dev.dtos.LikeDTO;
import com.example.zero2dev.services.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like")
public class LikeController {
    private final LikeService likeService;
    @PostMapping("/toggle")
    public ResponseEntity<?> toggleLike(@RequestBody LikeDTO likeDTO){
        return ResponseEntity.ok(this.likeService.toggleLike(likeDTO));
    }
}
