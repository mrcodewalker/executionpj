package com.example.zero2dev.models;

import com.example.zero2dev.storage.BlacklistReason;
import com.example.zero2dev.storage.TokenType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "blacklisted_token")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", length = 512, nullable = false, unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false)
    private TokenType tokenType;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private BlacklistReason reason;

    @Column(name = "username")
    private String username;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    @Column(name = "device_info", length = 255)
    private String deviceInfo;
}