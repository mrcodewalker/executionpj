package com.example.zero2dev.controllers;

import com.example.zero2dev.dtos.RefreshTokenDTO;
import com.example.zero2dev.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
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
            @RequestBody RefreshTokenDTO refreshTokenDTO,
            HttpServletRequest request){
        return ResponseEntity.ok(this.authenticationService.refreshToken(refreshTokenDTO.getRefreshToken(), request));
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request){
        authenticationService.logout(request);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
}
