package com.example.zero2dev.interfaces;

import com.example.zero2dev.dtos.PostDTO;
import com.example.zero2dev.responses.CustomPageResponse;
import com.example.zero2dev.responses.PostResponse;

import java.util.List;

public interface IPostService {
    PostResponse createPost(PostDTO postDTO);
    PostResponse updatePost(Long id, PostDTO postDTO);
    PostResponse deletePostById(Long postId);
    PostResponse getPostById(Long postId);
    CustomPageResponse<PostResponse> getPostByPage(int page, int size);
}
