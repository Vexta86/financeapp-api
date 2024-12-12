package com.api.financeapp.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthlyStatsDTO {

    private double totalIncome;
    private double totalExpenses;
    private double netIncome;

    private int month;
    private int year;

}
