package com.example.zero2dev.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFrameDTO {
    private Long userId;
    private Long frameId;
    private Boolean isActive;
}

