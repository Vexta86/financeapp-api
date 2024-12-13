package com.api.financeapp.dtos;

import com.api.financeapp.entities.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryStatsDTO {
    private CategoryDTO category;
    private double total;

    public CategoryStatsDTO(Category category, double total){
        this.category = new CategoryDTO(category);
        this.total = total;
    }

    public CategoryStatsDTO(){
    }
}
