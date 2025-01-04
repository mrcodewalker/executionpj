package com.example.zero2dev.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Data
public class UpdateFrameRequest {
    private String name;
    private String description;
    private String imageUrl;
    private String cssAnimation;
    private Long price;
    private Boolean isDefault;
}
