package com.example.zero2dev.responses;

import com.example.zero2dev.storage.TokenType;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RefreshTokenResponse {
    private Long id;
    private String token;
    private boolean revoked;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private LocalDateTime updatedAt;
}
