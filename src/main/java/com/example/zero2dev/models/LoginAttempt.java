package com.example.zero2dev.models;

import com.example.zero2dev.storage.LoginStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_attempt")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(name = "attempt_time",nullable = false)
    private LocalDateTime attemptTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LoginStatus status;
    @Column(name = "ip_address")
    private String ipAddress;
    @Column(name = "device_info")
    private String deviceInfo;
}
