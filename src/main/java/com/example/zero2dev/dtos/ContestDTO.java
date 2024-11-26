package com.example.zero2dev.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ContestDTO {
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String type;
}
