package com.example.zero2dev.responses;

import com.example.zero2dev.dtos.UserDTO;
import com.example.zero2dev.storage.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String author;
    private PostStatus status;
    private Long viewCount;
    private Long likesCount;
    private Long commentCount;
    private CommentResponse previewComment;
    private boolean isLiked;
    private LocalDateTime createdAt;
}