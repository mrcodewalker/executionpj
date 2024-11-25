package com.example.zero2dev.services;

import com.example.zero2dev.dtos.CategoryDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.interfaces.ICategoryService;
import com.example.zero2dev.models.Category;
import com.example.zero2dev.repositories.CategoryRepository;
import com.example.zero2dev.responses.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse createCategory(CategoryDTO categoryDTO) {
        Category category = Category.builder()
                .name(categoryDTO.getName())
                .build();
        category = this.categoryRepository.save(category);
        return CategoryResponse.builder()
                .name(category.getName())
                .build();
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = this.findCategoryById(id);
        Category cloneCategory = CategoryDTO.fromEntity(categoryDTO);
        cloneCategory.setId(id);
        this.categoryRepository.save(cloneCategory);
        return CategoryResponse.fromEntity(cloneCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        this.categoryRepository.deleteById(id);
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = this.findCategoryById(id);
        return CategoryResponse.fromEntity(category);
    }
    public Category findCategoryById(Long id){
        return this.categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }
}
