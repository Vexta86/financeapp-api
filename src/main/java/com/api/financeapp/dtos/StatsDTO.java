package com.api.financeapp.dtos;


import java.util.List;


public class StatsDTO {
    private double netWorth;
    private double averageIncome;
    private double averageExpenses;

    private double netIncome;
    private double totalIncome;
    private double totalExpenses;
    private double averageNetIncome;

    List<CategoryStatsDTO> statsPerIncomeCategory;
    List<CategoryStatsDTO> statsPerExpenseCategory;
    List<MonthlyStatsDTO> statsPerMonth;

    public double getTotalExpenses() {
        return totalExpenses;
    }

    public double getNetIncome() {
        return netIncome;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public double getAverageExpenses() {
        return averageExpenses;
    }

    public double getAverageIncome() {
        return averageIncome;
    }

    public double getAverageNetIncome() {
        return averageNetIncome;
    }

    public double getNetWorth() {
        return netWorth;
    }

    public List<CategoryStatsDTO> getStatsPerExpenseCategory() {
        return statsPerExpenseCategory;
    }

    public List<CategoryStatsDTO> getStatsPerIncomeCategory() {
        return statsPerIncomeCategory;
    }

    public List<MonthlyStatsDTO> getStatsPerMonth() {
        return statsPerMonth;
    }

    public void setTotalExpenses(double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public void setNetIncome(double netIncome) {
        this.netIncome = netIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public void setAverageExpenses(double averageExpenses) {
        this.averageExpenses = averageExpenses;
    }

    public void setAverageIncome(double averageIncome) {
        this.averageIncome = averageIncome;
    }

    public void setAverageNetIncome(double averageNetIncome) {
        this.averageNetIncome = averageNetIncome;
    }

    public void setNetWorth(double netWorth) {
        this.netWorth = netWorth;
    }

    public void setStatsPerExpenseCategory(List<CategoryStatsDTO> statsPerExpenseCategory) {
        this.statsPerExpenseCategory = statsPerExpenseCategory;
    }

    public void setStatsPerIncomeCategory(List<CategoryStatsDTO> statsPerIncomeCategory) {
        this.statsPerIncomeCategory = statsPerIncomeCategory;
    }

    public void setStatsPerMonth(List<MonthlyStatsDTO> statsPerMonth) {
        this.statsPerMonth = statsPerMonth;
    }

}
