package com.api.financeapp.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MonthlyAndCategoriesDTO {
    MonthlyStatsDTO monthlyStats;
    List<CategoryStatsDTO> incomeCategoryStats;
    List<CategoryStatsDTO> expenseCategoryStats;
}
