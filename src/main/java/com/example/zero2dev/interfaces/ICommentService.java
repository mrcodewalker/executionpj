package com.example.zero2dev.interfaces;

import com.example.zero2dev.dtos.CommentDTO;
import com.example.zero2dev.models.Post;
import com.example.zero2dev.responses.CommentResponse;
import com.example.zero2dev.responses.CustomPageResponse;

import java.util.List;

public interface ICommentService {
    CommentResponse createComment(CommentDTO commentDTO);
    CommentResponse updateComment(Long id, CommentDTO commentDTO);
    CommentResponse deleteCommentById(Long commentId);
    CommentResponse getCommentById(Long commentId);
    List<CommentResponse> getListComment(Long postId);
}
