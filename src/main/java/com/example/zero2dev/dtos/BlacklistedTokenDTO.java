package com.example.zero2dev.dtos;

import com.example.zero2dev.models.User;
import com.example.zero2dev.storage.BlacklistReason;
import com.example.zero2dev.storage.TokenType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BlacklistedTokenDTO {
    private String token;
    private TokenType tokenType;
    private BlacklistReason reason;
    private String ipAddress;
    private String deviceInfo;
}
