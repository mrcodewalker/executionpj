package com.example.zero2dev.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class UserRankingDTO {
    private Long ranking;
    private Long userId;
    private String username;
    private String avatarUrl;
    private Long totalExecutionTime;
    private Long totalMemoryUsed;
    private Long totalPoints;
}
