package com.example.zero2dev.dtos;

import com.example.zero2dev.models.AnimationData;
import com.example.zero2dev.storage.FrameType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Data
public class CreateFrameRequest {
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    private FrameType frameType;
    @NotBlank(message = "Image URL is required")
    private String imageUrl;
    private String cssAnimation;
    private Long price;
    private Boolean isDefault = false;
}
