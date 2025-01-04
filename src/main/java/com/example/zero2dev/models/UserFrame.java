package com.example.zero2dev.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_frame")
@Getter
@Setter
@NoArgsConstructor
public class UserFrame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "frame_id")
    private Frame frame;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @PrePersist
    protected void onCreate() {
        purchaseDate = LocalDateTime.now();
    }
}
