package com.api.financeapp.dtos;

import com.api.financeapp.entities.Category;
import com.api.financeapp.entities.CategoryType;

public record CategoryDTO(Long id, String name, CategoryType type) {
    public CategoryDTO(Category category) {
        this(category.getId(), category.getName(), category.getType());
    }
}
