package com.example.zero2dev.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String role;
    private String sessionId;
    @JsonProperty("token")
    private AuthenticationResponse authenticationResponse;
}
