package com.example.zero2dev.services;

import com.example.zero2dev.dtos.PostDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.interfaces.IPostService;
import com.example.zero2dev.models.Comment;
import com.example.zero2dev.models.Like;
import com.example.zero2dev.models.Post;
import com.example.zero2dev.models.User;
import com.example.zero2dev.repositories.PostRepository;
import com.example.zero2dev.responses.CustomPageResponse;
import com.example.zero2dev.responses.PostResponse;
import com.example.zero2dev.storage.MESSAGE;
import com.example.zero2dev.storage.PostStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {
    private final PostRepository postRepository;

    @Override
    public PostResponse createPost(PostDTO postDTO) {
        User user = SecurityService.getUserIdFromSecurityContext();
        if (user==null){
            throw new ResourceNotFoundException(MESSAGE.TOKEN_EXPIRED);
        }
        Post post = Post.builder()
                .content(postDTO.getContent())
                .title(postDTO.getTitle())
                .status(PostStatus.PUBLISHED)
                .viewCount(0L)
                .isActive(true)
                .user(user)
                .comments(new ArrayList<>())
                .likes(new ArrayList<>())
                .build();
        return this.toResponse(this.postRepository.save(post));
    }

    @Override
    public PostResponse updatePost(Long id, PostDTO postDTO) {
        Post post = this.validPost(id);
        if (postDTO.getContent()!=null){
            post.setContent(postDTO.getContent());
        }
        if (postDTO.getTitle()!=null){
            post.setTitle(postDTO.getTitle());
        }
        return this.toResponse(this.postRepository.save(post));
    }

    @Override
    public PostResponse deletePostById(Long postId) {
        Post post = this.validPost(postId);
        post.setIsActive(false);
        return this.toResponse(this.postRepository.save(post));
    }

    @Override
    public PostResponse getPostById(Long postId) {
        Post post = this.getPost(postId);
        if (!post.getIsActive()){
            throw new ResourceNotFoundException(MESSAGE.IP_BLACKLISTED);
        }
        return this.toResponse(this.getPost(postId));
    }

    @Override
    public CustomPageResponse<PostResponse> getPostByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = this.postRepository.findAllActive(pageable);
        return new CustomPageResponse<>(postPage.map(this::toResponse));
    }
    private PostResponse toResponse(Post post){
        List<Like> likes = post.getLikes();
        String username = this.getCurrentUserName();
        return PostResponse.builder()
                .id(post.getId())
                .status(post.getStatus())
                .author(post.getUser().getUsername())
                .commentCount((long) post.getComments().stream()
                        .filter(Comment::getIsActive)
                        .toList().size())
                .content(post.getContent())
                .title(post.getTitle())
                .likesCount((long) likes.stream()
                        .filter(Like::getIsActive)
                        .toList().size())
                .isLiked(likes.stream()
                        .anyMatch(like -> like.getIsActive() && like.getUser().getUsername().equals(username)))
                .viewCount(post.getViewCount())
                .createdAt(post.getCreatedAt())
                .build();
    }
    public Post getPost(Long postId){
        return this.postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
    private Post validPost(Long id){
        User user = SecurityService.getUserIdFromSecurityContext();
        if (user==null){
            throw new ResourceNotFoundException(MESSAGE.TOKEN_HAS_BEEN_REVOKED);
        }
        Post post = this.getPost(id);
        if (!post.getUser().getUsername().equalsIgnoreCase(user.getUsername())){
            throw new ResourceNotFoundException(MESSAGE.IP_BLACKLISTED);
        }
        return post;
    }
    private String getCurrentUserName(){
        User user = SecurityService.getUserIdFromSecurityContext();
        if (user==null){
            throw new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION);
        }
        return user.getUsername().toLowerCase();
    }
}
