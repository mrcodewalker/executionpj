package com.example.zero2dev.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FilterFrameDTO {
    private Long frameId;
    private String name;
    private String description;
    private String imageUrl;
    private String frameType;
    private String cssAnimation;
    private Long price;
    private Boolean isDefault;
    private Boolean isOwned;
    private Boolean isCurrent;
    private Long gems;
}
