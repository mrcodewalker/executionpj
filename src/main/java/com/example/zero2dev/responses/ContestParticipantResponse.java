package com.example.zero2dev.responses;

import com.example.zero2dev.models.Contest;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ContestParticipantResponse {
    private Contest contest;
    private Long userId;
    private Long totalScore;
    private LocalDateTime registeredTime;
}
