package com.example.zero2dev.models;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContestParticipantKey implements Serializable {
    private Long contestId;
    private Long userId;
}