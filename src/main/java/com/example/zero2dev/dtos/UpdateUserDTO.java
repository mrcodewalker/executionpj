package com.example.zero2dev.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UpdateUserDTO {
    private String username;
    private String phoneNumber;
    private String fullName;
    private String avatarUrl;
    private Long id;
}
