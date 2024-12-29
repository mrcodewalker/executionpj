package com.example.zero2dev.controllers;

import com.example.zero2dev.dtos.PostDTO;
import com.example.zero2dev.services.PostService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
public class PostController {
    private final PostService postService;
    @PostMapping("/create")
    public ResponseEntity<?> createPost(
            @RequestBody PostDTO postDTO){
        return ResponseEntity.ok(this.postService.createPost(postDTO));
    }
    @GetMapping("/filter/{id}")
    public ResponseEntity<?> getPostById(@PathVariable("id") Long id){
        return ResponseEntity.ok(this.postService.getPostById(id));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePostById(@PathVariable("id") Long id){
        return ResponseEntity.ok(this.postService.deletePostById(id));
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePost(@PathVariable("id") Long id,
                                        @RequestBody PostDTO postDTO){
        return ResponseEntity.ok(this.postService.updatePost(id, postDTO));
    }
    @GetMapping("/filter")
    public ResponseEntity<?> getPostPage(@RequestParam("page") int page,
                                         @RequestParam("size") int size){
        return ResponseEntity.ok(this.postService.getPostByPage(page, size));
    }
}
