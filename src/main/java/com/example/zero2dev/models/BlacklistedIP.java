package com.example.zero2dev.models;

import com.example.zero2dev.storage.BlacklistStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "blacklisted_ip")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlacklistedIP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ipAddress;

    @Column(nullable = false)
    private LocalDateTime blacklistedAt;

    @Column
    private String reason;

    @Column
    private LocalDateTime unblacklistAt;

    @Enumerated(EnumType.STRING)
    private BlacklistStatus status;
}

