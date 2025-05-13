package com.api.financeapp.dtos;



public class MonthlyStatsDTO {

    private double totalIncome;
    private double totalExpenses;
    private double netIncome;

    private int month;
    private int year;

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public double getNetIncome() {
        return netIncome;
    }

    public double getTotalExpenses() {
        return totalExpenses;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setNetIncome(double netIncome) {
        this.netIncome = netIncome;
    }

    public void setTotalExpenses(double totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
