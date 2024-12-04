package com.example.zero2dev.interfaces;

import com.example.zero2dev.dtos.CategoryDTO;
import com.example.zero2dev.dtos.ProblemDTO;
import com.example.zero2dev.models.Category;
import com.example.zero2dev.responses.CategoryResponse;
import com.example.zero2dev.responses.ProblemResponse;

import java.util.List;

public interface ICategoryService {
    CategoryResponse createCategory(CategoryDTO categoryDTO);
    CategoryResponse updateCategory(Long id, CategoryDTO categoryDTO);
    void deleteCategory(Long id);
    CategoryResponse getCategoryById(Long id);
    List<CategoryResponse> getListCategory();
}
