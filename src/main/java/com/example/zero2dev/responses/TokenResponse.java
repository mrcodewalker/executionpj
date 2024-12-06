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
public class TokenResponse {
    private Long id;
    private String token;
    private TokenType tokenType;
    private boolean revoked;
    private boolean expired;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
    private LocalDateTime updatedAt;
}
