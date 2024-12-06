package com.example.zero2dev.controllers;

import com.example.zero2dev.models.BlacklistedIP;
import com.example.zero2dev.services.BlacklistedIPService;
import com.example.zero2dev.services.BlacklistedTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/blacklisted_ip")
@RequiredArgsConstructor
public class BlacklistedIPController {
    private final BlacklistedIPService blacklistedIPService;
    @PostMapping("/filter")
    public ResponseEntity<?> filterToken(){
        return ResponseEntity.ok(this.blacklistedIPService.filterList());
    }
    @PostMapping("/unban/{id}")
    public ResponseEntity<?> unbanIP(
            @PathVariable("id") Long id){
        this.blacklistedIPService.unbanIP(id);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
    @PostMapping("/unban/ip/{id}")
    public ResponseEntity<?> unbanIPAddress(
            @PathVariable("id") String ipAddress){
        this.blacklistedIPService.unbanIP(ipAddress);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
    @PostMapping("/ban/ip/{id}")
    public ResponseEntity<?> banIPAddress(
            @PathVariable("id") String ipAddress){
        this.blacklistedIPService.banIP(ipAddress);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
    @PostMapping("/ban/{id}")
    public ResponseEntity<?> banIP(
            @PathVariable("id") Long id){
        this.blacklistedIPService.banIP(id);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
}
