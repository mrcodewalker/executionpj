package com.example.zero2dev.controllers;

import com.example.zero2dev.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/refresh_token")
@RequiredArgsConstructor
public class RefreshTokenController {
    private final AuthenticationService authenticationService;
    @PostMapping("/filter")
    public ResponseEntity<?> filterToken(){
        return ResponseEntity.ok(this.authenticationService.filterList());
    }
}
