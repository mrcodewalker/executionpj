package com.example.zero2dev.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnimationData {
    private List<String> frames;
    private Integer frameRate;
    private String cssAnimation;
    private Map<String, Object> webglData;
}
