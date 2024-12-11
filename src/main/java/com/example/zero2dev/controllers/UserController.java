package com.example.zero2dev.controllers;

import com.example.zero2dev.dtos.LoginDTO;
import com.example.zero2dev.dtos.UpdateUserDTO;
import com.example.zero2dev.dtos.UserDTO;
import com.example.zero2dev.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.PreparedStatement;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UserController {
    private final UserService userService;
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        this.userService.resetPassword(token, newPassword);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        return this.userService.forgotPassword(email);
    }
    @PostMapping("/register")
    public ResponseEntity<?> createUser(
            @RequestBody UserDTO userDTO) throws MessagingException {
        return ResponseEntity.ok(
                this.userService.createUser(userDTO));
    }
    @GetMapping("/verify")
    public void verifyToken(@RequestParam String token,
                            @RequestParam String username,
                            HttpServletResponse response) throws IOException {
        if (token != null && !token.isEmpty()) {
            this.userService.verifyEmail(token, username);
            response.sendRedirect("http://localhost:4200/login?secretCode=HAISIEUDEPTRAIVUTRU");
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid or expired token.");
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @RequestBody LoginDTO loginDTO,
            HttpServletRequest request){
        return ResponseEntity.ok(this.userService.login(loginDTO, request));
    }
    @PostMapping("/update/info")
    public ResponseEntity<?> updateUserInfo(
            @RequestBody UpdateUserDTO updateUserDTO){
        return ResponseEntity.ok(
                this.userService.updateUser(updateUserDTO));
    }
    @PostMapping("/ban")
    public ResponseEntity<?> deleteById(
            @RequestParam("id") Long id){
        return ResponseEntity.ok(this.userService.deleteUserById(id));
    }
    @PostMapping("/list/available")
    public ResponseEntity<?> getListUserAvailable(){
        return ResponseEntity.ok(this.userService.getUserAvailable());
    }
    @PostMapping("/list/unavailable")
    public ResponseEntity<?> getListUserUnAvailable(){
        return ResponseEntity.ok(this.userService.getUnavailableUsers());
    }
    @PostMapping("/collect/list")
    public ResponseEntity<?> getAllUsers(){
        return ResponseEntity.ok(this.userService.getAllUsers());
    }
    @PostMapping("/list/highest")
    public ResponseEntity<?> getHighestListUser(){
        return ResponseEntity.ok(this.userService.getUserHighestScore());
    }
    @GetMapping("/view/{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable("id") Long id){
        return ResponseEntity.ok(this.userService.getUserById(id));
    }
    @GetMapping("/info/mail")
    public ResponseEntity<?> getUserByEmail(
            @RequestParam("email") String email){
        return ResponseEntity.ok(this.userService.getUserByEmail(email));
    }

    @GetMapping("/info/phone")
    public ResponseEntity<?> getUserByPhoneNumber(
            @RequestParam("phoneNumber") String phoneNumber){
        return ResponseEntity.ok(this.userService.getUserByPhoneNumber(phoneNumber));
    }
    @GetMapping("/info/name")
    public ResponseEntity<?> getUserByUserName(
            @RequestParam("username") String username){
        return ResponseEntity.ok(this.userService.getUserByUserName(username));
    }
    @GetMapping("/filter/email")
    public ResponseEntity<?> filterByEmail(
            @RequestParam("email") String email){
        return ResponseEntity.ok(this.userService.getUserByMatchEmail(email));
    }
    @GetMapping("/filter/phone_number")
    public ResponseEntity<?> filterByPhoneNumber(
            @RequestParam("phoneNumber") String phoneNumber){
        return ResponseEntity.ok(this.userService.getUserByMatchPhoneNumber(phoneNumber));
    }
    @GetMapping("/filter/username")
    public ResponseEntity<?> filterByUserName(
            @RequestParam("username") String username){
        return ResponseEntity.ok(this.userService.getUserByMatchUserName(username));
    }
}
