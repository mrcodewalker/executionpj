package com.example.zero2dev.services;

import com.example.zero2dev.dtos.CategoryDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.interfaces.ICategoryService;
import com.example.zero2dev.mapper.CategoryMapper;
import com.example.zero2dev.models.Category;
import com.example.zero2dev.repositories.CategoryRepository;
import com.example.zero2dev.responses.CategoryResponse;
import com.example.zero2dev.storage.MESSAGE;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;
    @Override
    public CategoryResponse createCategory(CategoryDTO categoryDTO) {
        return CategoryResponse.toResponse(this.categoryRepository.save(mapper.toEntity(categoryDTO)));
    }
    @Override
    public CategoryResponse updateCategory(Long id, CategoryDTO categoryDTO) {
        Category cloneCategory = mapper.parseEntity(this.findCategoryById(id), categoryDTO);
        return CategoryResponse.toResponse(this.categoryRepository.save(cloneCategory));
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = this.findCategoryById(id);
        this.categoryRepository.delete(category);
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        return CategoryResponse.toResponse(this.findCategoryById(id));
    }

    @Override
    public List<CategoryResponse> getListCategory() {
        return Optional.of(this.categoryRepository.findAll())
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(CategoryResponse::toResponse)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }

    public Category findCategoryById(Long id){
        return this.categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
}
