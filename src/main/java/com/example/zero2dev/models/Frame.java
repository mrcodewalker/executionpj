package com.example.zero2dev.models;

import com.example.zero2dev.storage.FrameType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "frame")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Frame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 255)
    private String imageUrl;
    @Column(name = "frame_type")
    @Enumerated(EnumType.STRING)
    private FrameType frameType = FrameType.STATIC;
    @Column(name = "css_animation")
    private String cssAnimation;
    @Column(name = "price", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long price;

    @Column(name = "is_default", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isDefault;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreatedAt() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}

