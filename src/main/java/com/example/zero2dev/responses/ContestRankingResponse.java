package com.example.zero2dev.responses;

import com.example.zero2dev.models.ContestRanking;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContestRankingResponse {
    private Long id;
    private Long contestId;
    private Long userId;
    private Long rank;
    private Long score;
    public static ContestRankingResponse exchangeEntity(ContestRanking contestRanking){
        return ContestRankingResponse.builder()
                .contestId(contestRanking.getContest().getId())
                .rank(contestRanking.getRank())
                .id(contestRanking.getId())
                .score(contestRanking.getScore())
                .userId(contestRanking.getUser().getId())
                .build();
    }
}
