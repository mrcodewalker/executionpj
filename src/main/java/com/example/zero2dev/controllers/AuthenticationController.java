package com.example.zero2dev.controllers;

import com.example.zero2dev.dtos.RefreshTokenDTO;
import com.example.zero2dev.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @PostMapping("/refresh_token")
    public ResponseEntity<?> refreshToken(
            @RequestBody RefreshTokenDTO refreshTokenDTO){
        return ResponseEntity.ok(this.authenticationService.refreshToken(refreshTokenDTO.getRefreshToken()));
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(){
        authenticationService.logout();
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
}
