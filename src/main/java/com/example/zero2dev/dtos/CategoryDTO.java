package com.example.zero2dev.dtos;

import com.example.zero2dev.models.Category;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CategoryDTO {
    private String name;
    public static Category fromEntity(CategoryDTO categoryDTO){
        return Category.builder()
                .name(categoryDTO.getName())
                .build();
    }
}
