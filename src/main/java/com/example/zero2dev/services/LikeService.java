package com.example.zero2dev.services;

import com.example.zero2dev.dtos.LikeDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.interfaces.ILikeService;
import com.example.zero2dev.models.Like;
import com.example.zero2dev.models.Post;
import com.example.zero2dev.models.User;
import com.example.zero2dev.repositories.LikeRepository;
import com.example.zero2dev.repositories.PostRepository;
import com.example.zero2dev.responses.LikeResponse;
import com.example.zero2dev.storage.MESSAGE;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LikeService implements ILikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    @Override
    public LikeResponse toggleLike(LikeDTO likeDTO) {
        Like like = this.getToggleLike(likeDTO.getPostId(), this.collectUserId());
        if (like==null){
            like = Like.builder()
                    .user(this.getUser())
                    .post(this.validPost(likeDTO.getPostId()))
                    .isActive(true)
                    .build();
        } else {
            like.setIsActive(!like.getIsActive());
        }
        this.likeRepository.save(like);
        return LikeResponse.builder()
                .message("Action successfully!")
                .status((long) 666)
                .build();
    }

    @Override
    public LikeResponse unlike(LikeDTO likeDTO) {
        Like like = this.getByUserIdAndPostId(likeDTO.getPostId(), collectUserId());
        like.setIsActive(false);
        this.likeRepository.save(like);
        return LikeResponse.builder()
                .message("Unlike successfully!")
                .status((long) 666)
                .build();
    }
    private Like getByUserIdAndPostId(Long postId, Long userId){
        return this.likeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.IP_BLACKLISTED));
    }
    private Like getToggleLike(Long postId, Long userId){
        return this.likeRepository.findByPostIdAndUserId(postId, userId)
                .orElse(null);
    }
    private Long collectUserId(){
        User user = SecurityService.getUserIdFromSecurityContext();
        if (user==null){
            throw new ResourceNotFoundException(MESSAGE.IP_BLACKLISTED);
        }
        return user.getId();
    }
    private User getUser(){
        User user = SecurityService.getUserIdFromSecurityContext();
        if (user==null){
            throw new ResourceNotFoundException(MESSAGE.IP_BLACKLISTED);
        }
        return user;
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
}
