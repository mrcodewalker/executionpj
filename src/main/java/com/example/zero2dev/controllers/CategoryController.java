package com.example.zero2dev.controllers;

import com.example.zero2dev.dtos.CategoryDTO;
import com.example.zero2dev.dtos.ProblemDTO;
import com.example.zero2dev.models.Problem;
import com.example.zero2dev.responses.CategoryResponse;
import com.example.zero2dev.responses.ProblemResponse;
import com.example.zero2dev.services.CategoryService;
import com.example.zero2dev.services.ProblemService;
import com.example.zero2dev.storage.Difficulty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    @PostMapping("/create")
    public ResponseEntity<?> createCategory(
            @RequestBody CategoryDTO categoryDTO){
        CategoryResponse categoryResponse = this.categoryService.createCategory(
                categoryDTO
        );
        return new ResponseEntity<>(categoryResponse, HttpStatus.CREATED);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCategory(
            @PathVariable("id") Long id,
            @RequestBody CategoryDTO categoryDTO
    ){
        return ResponseEntity.ok(
                this.categoryService.updateCategory(id , categoryDTO)
        );
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCategory(
            @PathVariable("id") Long id
    ){
        this.categoryService.deleteCategory(id);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getCategory(
            @PathVariable("id") Long id
    ){
        return ResponseEntity.ok(this.categoryService.getCategoryById(id));
    }
}