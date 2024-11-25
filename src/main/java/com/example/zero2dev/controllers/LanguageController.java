package com.example.zero2dev.controllers;

import com.example.zero2dev.dtos.LanguageDTO;
import com.example.zero2dev.services.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/language")
public class LanguageController {
    private final LanguageService languageService;
    @PostMapping("/create")
    public ResponseEntity<?> createLanguage(
            @RequestBody LanguageDTO languageDTO
    ){
        return ResponseEntity.ok(this.languageService.createLanguage(languageDTO));
    }
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getLanguage(
            @PathVariable("id") Long id
    ){
        return ResponseEntity.ok(this.languageService.getLanguageById(id));
    }
    @GetMapping("/get/list")
    public ResponseEntity<?> getAllLanguages(){
        return ResponseEntity.ok(
                this.languageService.getAllLanguage()
        );
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(
            @PathVariable("id") Long id
    ){
        return ResponseEntity.ok(this.languageService.deleteById(id));
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateById(
            @PathVariable("id") Long id,
            @RequestBody LanguageDTO languageDTO
    ){
        return ResponseEntity.ok(this.languageService.updateLanguage(id, languageDTO));
    }
}
