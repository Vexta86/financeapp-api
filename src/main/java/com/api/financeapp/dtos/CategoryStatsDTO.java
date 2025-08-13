package com.api.financeapp.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryStatsDTO {
    private String category;
    private double total;

    public CategoryStatsDTO(String category, double total){
        this.category = category;
        this.total = total;
    }

    public CategoryStatsDTO(){
    }


}
