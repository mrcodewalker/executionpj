package com.example.zero2dev.controllers;

import com.example.zero2dev.services.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/token")
@RequiredArgsConstructor
public class TokenController {
    private final TokenService tokenService;
    @PostMapping("/filter")
    public ResponseEntity<?> filterToken(){
        return ResponseEntity.ok(this.tokenService.filterToken());
    }
}
