package com.example.zero2dev.responses;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;
    private LocalDateTime expiredAccessToken;
    private LocalDateTime expiredRefreshToken;
}
