package com.example.zero2dev.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "role")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "role_name", nullable = false)
    private String roleName;
    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    private LocalDateTime updatedAt;
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
    public static final String MANAGER = "MANAGER";
    public static final String DEVELOPER = "DEVELOPER";
    public static String[] fullRoleAccess() {
        return new String[] {
                Role.USER,
                Role.ADMIN,
                Role.DEVELOPER,
                Role.MANAGER,
        };
    }
    public static String[] teamAccess() {
        return new String[] {
                Role.ADMIN,
                Role.DEVELOPER,
                Role.MANAGER
        };
    }
    public static String[] managerAccess(){
        return new String[] {
                Role.ADMIN,
                Role.MANAGER
        };
    }
    public static String[] developerAccess(){
        return new String[] {
                Role.ADMIN,
                Role.DEVELOPER
        };
    }
    public static String adminAccess(){
        return Role.ADMIN;
    }
}
