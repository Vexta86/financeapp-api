package com.api.financeapp.dtos;

import com.api.financeapp.entities.Category;
import com.api.financeapp.entities.CategoryType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {
    private Long id;
    private String name;
    private CategoryType type;
    public CategoryDTO(Category category){
        // Populate the DTO with data from the category entity
        this.setId(category.getId());
        this.setName(category.getName());
        this.setType(category.getType());
    }
}
