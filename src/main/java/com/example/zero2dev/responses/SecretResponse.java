package com.example.zero2dev.responses;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SecretResponse {
    private Long status;
    private String message;
    private String[] errors;
}
