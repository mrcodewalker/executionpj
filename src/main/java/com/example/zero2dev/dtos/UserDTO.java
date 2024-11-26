package com.example.zero2dev.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserDTO {
    private String username;
    private String password;
    private String email;
    private String avatarUrl;
    private String phoneNumber;
    private String fullName;
}
