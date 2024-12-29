package com.example.zero2dev.services;

import com.example.zero2dev.dtos.CommentDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.interfaces.ICommentService;
import com.example.zero2dev.models.Comment;
import com.example.zero2dev.models.Post;
import com.example.zero2dev.models.User;
import com.example.zero2dev.repositories.CommentRepository;
import com.example.zero2dev.repositories.PostRepository;
import com.example.zero2dev.responses.CommentResponse;
import com.example.zero2dev.responses.CustomPageResponse;
import com.example.zero2dev.storage.MESSAGE;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    @Override
    public CommentResponse createComment(CommentDTO commentDTO) {
        User user = SecurityService.getUserIdFromSecurityContext();
        if (user==null){
            throw new ResourceNotFoundException(MESSAGE.IP_BLACKLISTED);
        }
        Comment comment = Comment.builder()
                .post(this.validPost(commentDTO.getPostId()))
                .user(user)
                .content(commentDTO.getContent())
                .isActive(true)
                .build();
        return this.toResponse(this.commentRepository.save(comment));
    }

    @Override
    public CommentResponse updateComment(Long id, CommentDTO commentDTO) {
        Pair<Comment, String> data = this.validComment(id);
        Comment comment = data.getFirst();
        Post post = this.validPost(commentDTO.getPostId());
        if (!comment.getUser().getUsername().equalsIgnoreCase(data.getSecond())){
            throw new ResourceNotFoundException(MESSAGE.IP_BLACKLISTED);
        }
        if (commentDTO.getContent()!=null){
            comment.setContent(commentDTO.getContent());
        }
        comment.setPost(post);
        return this.toResponse(this.commentRepository.save(comment));
    }

    @Override
    public CommentResponse deleteCommentById(Long commentId) {
        Comment comment = this.validComment(commentId).getFirst();
        comment.setIsActive(false);
        return this.toResponse(this.commentRepository.save(comment));
    }

    @Override
    public CommentResponse getCommentById(Long commentId) {
        return this.toResponse(this.validComment(commentId).getFirst());
    }

    @Override
    public List<CommentResponse> getListComment(Long postId) {
        return this.commentRepository.findByPostId(postId)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    private CommentResponse toResponse(Comment comment){
        return CommentResponse.builder()
                .id(comment.getId())
                .author(comment.getUser().getUsername())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
    private Pair<Comment, String> validComment(Long id){
        User user = SecurityService.getUserIdFromSecurityContext();
        if (user==null){
            throw new ResourceNotFoundException(MESSAGE.IP_BLACKLISTED);
        }
        Comment comment = this.getComment(id);
        if (!comment.getUser().getUsername().equalsIgnoreCase(user.getUsername())|| !comment.getIsActive()) {
            throw new ResourceNotFoundException(MESSAGE.IP_BLACKLISTED);
        }
        return Pair.of(comment, user.getUsername());
    }
    private Comment getComment(Long id){
        return this.commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.IP_BLACKLISTED));
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
