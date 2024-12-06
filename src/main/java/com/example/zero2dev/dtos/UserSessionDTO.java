package com.example.zero2dev.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserSessionDTO {
    private Long id;
    private Long userId;
    private String sessionId;
    private String ipAddress;
    private LocalDateTime createdAt;
    private LocalDateTime lastActiveAt;
    private LocalDateTime expiredAt;
    private Boolean isActive;
    private String deviceInfo;
}
