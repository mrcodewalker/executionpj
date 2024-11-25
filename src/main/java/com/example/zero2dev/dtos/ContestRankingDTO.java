package com.example.zero2dev.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ContestRankingDTO {
    private Long contestId;
    private Long userId;
    private Long rank;
    private Long score;
}
