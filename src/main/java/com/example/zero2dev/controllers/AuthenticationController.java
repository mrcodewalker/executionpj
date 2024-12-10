package com.example.zero2dev.controllers;

import com.example.zero2dev.dtos.RefreshTokenDTO;
import com.example.zero2dev.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

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
    @GetMapping("/verify")
    public void verifyToken(@RequestParam String token, HttpServletResponse response) throws IOException {
        if (token != null && !token.isEmpty()) {
            response.sendRedirect("http://localhost:4200/login?secretCode=HAISIEUDEPTRAIVUTRU");
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid or expired token.");
        }
    }
}
