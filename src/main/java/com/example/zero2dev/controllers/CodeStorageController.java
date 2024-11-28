package com.example.zero2dev.controllers;

import com.example.zero2dev.dtos.CodeStorageDTO;
import com.example.zero2dev.services.CodeStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/code/storage")
@RequiredArgsConstructor
public class CodeStorageController {
    private final CodeStorageService codeStorageService;
    @PostMapping("/create")
    public ResponseEntity<?> createCodeStorage(
            @RequestBody CodeStorageDTO codeStorageDTO){
        return ResponseEntity.ok(this.codeStorageService.createCodeStorage(codeStorageDTO));
    }
    @GetMapping("/info")
    public ResponseEntity<?> getCodeStorage(
            @RequestBody CodeStorageDTO codeStorageDTO){
        return ResponseEntity.ok(this.codeStorageService.getByInfo(codeStorageDTO));
    }
    @PostMapping("/filter")
    public ResponseEntity<?> filterListBySize(
            @RequestParam(value = "size", defaultValue = "0") int size,
            @RequestBody CodeStorageDTO codeStorageDTO){
        return ResponseEntity.ok(this.codeStorageService.getCodeStorageBySize(size, codeStorageDTO));
    }
    @GetMapping("/filter/user/{id}")
    public ResponseEntity<?> filterByUserId(
            @PathVariable("id") Long id){
        return ResponseEntity.ok(this.codeStorageService.getCodeStorageByUser(id));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(
            @PathVariable("id") Long id){
        this.codeStorageService.deleteCodeStorageById(id);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
}
