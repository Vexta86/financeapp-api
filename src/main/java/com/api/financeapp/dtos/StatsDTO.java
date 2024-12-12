package com.api.financeapp.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StatsDTO {
    private double netWorth;
    private double averageIncome;
    private double averageExpenses;

    private double netIncome;
    private double totalIncome;
    private double totalExpenses;
    private double averageNetIncome;

    private List<MonthlyStatsDTO> monthlyStats;



}
