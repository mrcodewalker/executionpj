package com.example.zero2dev.responses;

import com.example.zero2dev.models.ContestRanking;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContestRankingResponse {
    private Long contestId;
    private Long userId;
    private String userName;
    private Long rank;
    private Long totalScore;
    private Long totalExecutionTime;
    private Long totalMemoryUsed;
    public static ContestRankingResponse exchangeEntity(ContestRanking contestRanking){
        return ContestRankingResponse.builder()
                .contestId(contestRanking.getContest().getId())
                .rank(contestRanking.getRank())
                .totalScore(contestRanking.getScore())
                .userId(contestRanking.getUser().getId())
                .build();
    }
}
