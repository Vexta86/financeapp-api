package com.api.financeapp.dtos;

import com.api.financeapp.entities.CategoryType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {
    private Long id;
    private String name;
    private CategoryType type;
}
