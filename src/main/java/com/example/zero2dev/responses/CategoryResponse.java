package com.example.zero2dev.responses;

import com.example.zero2dev.models.Category;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {
    private String name;
    public static CategoryResponse fromEntity(Category category){
        return CategoryResponse.builder()
                .name(category.getName())
                .build();
    }
}
