package com.example.zero2dev.controllers;

import com.example.zero2dev.services.BlacklistedTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/blacklisted_token")
@RequiredArgsConstructor
public class BlacklistedTokenController {
    private final BlacklistedTokenService blacklistedTokenService;
    @PostMapping("/filter")
    public ResponseEntity<?> filterToken(){
        return ResponseEntity.ok(this.blacklistedTokenService.filterList());
    }
}
