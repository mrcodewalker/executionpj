package com.example.zero2dev.mapper;

import com.example.zero2dev.dtos.CategoryDTO;
import com.example.zero2dev.models.Category;
import com.example.zero2dev.responses.CategoryResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CategoryDTO categoryDTO);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Category parseEntity(@MappingTarget Category category, CategoryDTO categoryDTO);
    CategoryResponse toResponse(Category category);
}
