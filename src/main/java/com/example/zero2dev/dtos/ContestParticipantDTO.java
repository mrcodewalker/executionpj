package com.example.zero2dev.dtos;

import com.example.zero2dev.models.ContestParticipant;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ContestParticipantDTO {
    private Long contestId;
    private Long userId;
    private Long totalScore;
}
