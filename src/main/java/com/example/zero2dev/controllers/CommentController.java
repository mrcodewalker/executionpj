package com.example.zero2dev.controllers;

import com.example.zero2dev.dtos.CommentDTO;
import com.example.zero2dev.models.Post;
import com.example.zero2dev.services.CommentService;
import com.example.zero2dev.services.PostService;
import com.example.zero2dev.services.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentController {
    private final CommentService commentService;
    @PostMapping("/create")
    public ResponseEntity<?> createComment(@RequestBody CommentDTO commentDTO){
        return ResponseEntity.ok(this.commentService.createComment(commentDTO));
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateComment(@PathVariable("id") Long id,
                                           @RequestBody CommentDTO commentDTO){
        return ResponseEntity.ok(this.commentService.updateComment(id, commentDTO));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable("id") Long id){
        return ResponseEntity.ok(this.commentService.deleteCommentById(id));
    }
    @GetMapping("/filter/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable("id") Long id){
        return ResponseEntity.ok(this.commentService.getCommentById(id));
    }
    @GetMapping("/filter")
    public ResponseEntity<?> getCommentByPostId(@RequestParam("id") Long id){
        return ResponseEntity.ok(this.commentService.getListComment(id));
    }
}
