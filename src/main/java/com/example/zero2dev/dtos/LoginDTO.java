package com.example.zero2dev.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LoginDTO {
    private String username;
    private String password;
}
