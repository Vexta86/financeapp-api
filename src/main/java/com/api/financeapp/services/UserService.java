package com.api.financeapp.services;

import com.api.financeapp.dtos.*;

import com.api.financeapp.entities.CategoryType;
import com.api.financeapp.entities.User;
import com.api.financeapp.repositories.SingleTransactionRepository;
import com.api.financeapp.repositories.UserRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SingleTransactionService transactionService;
    private final SingleTransactionRepository singleTransactionRepository;

    public UserService(UserRepository userRepository, SingleTransactionService transactionService, SingleTransactionRepository singleTransactionRepository) {
        this.userRepository = userRepository;
        this.transactionService = transactionService;
        this.singleTransactionRepository = singleTransactionRepository;
    }


    /**
     * Finds a user by their email address.
     *
     * @param emailAddress Email address of the user
     * @return Optional user entity
     */
    public Optional<User> findByEmailAddress(String emailAddress) {
        return userRepository.findByEmailAddress(emailAddress);
    }

    /**
     * Converts a user entity to a user DTO.
     *
     * @param user User entity
     * @return User DTO
     */
    public UserDTO convertUserToDto(User user){
        UserDTO dto = new UserDTO();
        dto.setEmailAddress(user.getEmailAddress());
        dto.setName(user.getName());
        dto.setLastName(user.getLastName());
        dto.setRole(String.valueOf(user.getRole()));

        // Calculate net worth using transaction service
        dto.setNetWorth(transactionService.getNetWorth(user));
        return dto;
    }


    /**
     * Retrieves user statistics for a specified number of previous months.
     *
     * @param user           User entity
     * @param previousMonths Number of previous months to include in statistics
     * @return User statistics DTO
     */
    public StatsDTO getStats(User user, int previousMonths) {
        // Initialize user statistics
        StatsDTO statsDTO = new StatsDTO();

        // Initialize list to store monthly statistics
        List<MonthlyStatsDTO> monthlyStatsDTOS = new ArrayList<>();

        // Get current date
        LocalDate currentDate = LocalDate.now();

        // Iterate through previous months
        for (int i = 0; i < previousMonths; i++) {
            // Calculate first and last day of previous month
            LocalDate firstDayOfPreviousMonth = currentDate.minusMonths(i).with(TemporalAdjusters.firstDayOfMonth());
            LocalDate lastDayOfPreviousMonth = currentDate.minusMonths(i).with(TemporalAdjusters.lastDayOfMonth());

            // Retrieve income and expenses for previous month using repository methods
            Double income = singleTransactionRepository
                    .sumIncomeByUserBetween(user, firstDayOfPreviousMonth, lastDayOfPreviousMonth);

            Double expenses = singleTransactionRepository
                    .sumExpensesByUserBetween(user, firstDayOfPreviousMonth, lastDayOfPreviousMonth);

            // Create monthly statistics DTO
            MonthlyStatsDTO monthlyStatsDTO = getMonthlyStatsDTO(firstDayOfPreviousMonth, income, expenses);

            monthlyStatsDTOS.add(monthlyStatsDTO);
        }

        // Calculate total income, expenses, and net income
        double totalIncome = monthlyStatsDTOS.stream().mapToDouble(MonthlyStatsDTO::getTotalIncome).sum();
        double totalExpenses = monthlyStatsDTOS.stream().mapToDouble(MonthlyStatsDTO::getTotalExpenses).sum();
        double totalNetIncome = monthlyStatsDTOS.stream().mapToDouble(MonthlyStatsDTO::getNetIncome).sum();

        // Calculate average income, expenses, and net income
        int monthCount = monthlyStatsDTOS.size();
        double averageIncome = monthCount > 0 ? totalIncome / monthCount : 0;
        double averageExpenses = monthCount > 0 ? totalExpenses / monthCount : 0;
        double averageNetIncome = monthCount > 0 ? totalNetIncome / monthCount : 0;

        // Set user statistics
        statsDTO.setTotalExpenses(totalExpenses);
        statsDTO.setTotalIncome(BigDecimal.valueOf(totalIncome)
                .setScale(2, RoundingMode.HALF_UP).doubleValue());
        statsDTO.setNetIncome(BigDecimal.valueOf(totalNetIncome)
                .setScale(2, RoundingMode.HALF_UP).doubleValue());
        statsDTO.setAverageExpenses(BigDecimal.valueOf(averageExpenses)
                .setScale(2, RoundingMode.HALF_UP).doubleValue());
        statsDTO.setAverageIncome(BigDecimal.valueOf(averageIncome)
                .setScale(2, RoundingMode.HALF_UP).doubleValue());
        statsDTO.setAverageNetIncome(BigDecimal.valueOf(averageNetIncome)
                .setScale(2, RoundingMode.HALF_UP).doubleValue());
        statsDTO.setNetWorth(transactionService.getNetWorth(user));
        statsDTO.setMonthlyStats(monthlyStatsDTOS);

        return statsDTO;
    }
    /**
     * Retrieves the statistics and category-wise statistics (income and expenses)
     * for a given user in a range date.
     *
     * @param user the user for whom the statistics are calculated
     * @param startDate a localDate representing the start date
     * @param endDate a localDate representing the end date
     * @return a DTO containing statistics and category-wise statistics
     */
    public MonthlyAndCategoriesDTO getStatsBetween(User user, LocalDate startDate, LocalDate endDate){
        MonthlyAndCategoriesDTO dto = new MonthlyAndCategoriesDTO();

        // Retrieve total income for the month; ensure null values are treated as zero
        Double income = singleTransactionRepository
                .sumIncomeByUserBetween(user, startDate, endDate);

        // Retrieve total expenses for the month; ensure null values are treated as zero
        Double expenses = singleTransactionRepository
                .sumExpensesByUserBetween(user, startDate, endDate);
        // populate the monthly stats dto
        MonthlyStatsDTO statsDTO = getMonthlyStatsDTO(startDate, income, expenses);

        // Populate category-wise income statistics
        List<CategoryStatsDTO> categoryIncomeStats = singleTransactionRepository
                .sumByCategoryAndUserBetween(user, CategoryType.INCOME, startDate, endDate)
                .stream()
                .map(result -> {
                    // Map the query result to a CategoryStatsDTO
                    CategoryStatsDTO categoryStatsDTO = new CategoryStatsDTO();
                    categoryStatsDTO.setCategory(result.getCategory()); // Map the category
                    categoryStatsDTO.setTotal(result.getTotal()); // Set the total for this category
                    return categoryStatsDTO;
                })
                .toList();

        // Populate category-wise expense statistics
        List<CategoryStatsDTO> categoryExpenseStats = singleTransactionRepository
                .sumByCategoryAndUserBetween(user, CategoryType.EXPENSE, startDate, endDate)
                .stream()
                .map(result -> {
                    // Map the query result to a CategoryStatsDTO
                    CategoryStatsDTO categoryStatsDTO = new CategoryStatsDTO();
                    categoryStatsDTO.setCategory(result.getCategory()); // Map the category
                    categoryStatsDTO.setTotal(result.getTotal()); // Set the total for this category
                    return categoryStatsDTO;
                })
                .toList();

        // Set the calculated statistics in the final DTO
        dto.setMonthlyStats(statsDTO);
        dto.setIncomeCategoryStats(categoryIncomeStats);
        dto.setExpenseCategoryStats(categoryExpenseStats);

        return dto;

    }
    /**
     * Retrieves the monthly statistics and category-wise statistics (income and expenses)
     * for a given user and date.
     *
     * @param user the user for whom the statistics are calculated
     * @param date a string representing the date (YYYY-MM-DD)
     * @return a DTO containing monthly statistics and category-wise statistics
     */
    public MonthlyAndCategoriesDTO getStatsThisMonth(User user, String date) {

        // Parse the provided date and determine the first and last days of the month
        LocalDate currentDate = LocalDate.parse(date);
        LocalDate firstDayOfPreviousMonth = currentDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfPreviousMonth = currentDate.with(TemporalAdjusters.lastDayOfMonth());


        return getStatsBetween(user, firstDayOfPreviousMonth, lastDayOfPreviousMonth);
    }

    public MonthlyAndCategoriesDTO getStatsBetween(User user, String givenStartDate, String givenEndDate){
        LocalDate startDate = LocalDate.parse(givenStartDate);
        LocalDate endDate = LocalDate.parse(givenEndDate);

        return getStatsBetween(user, startDate, endDate);
    }

    /**
     * Creates a MonthlyStatsDTO object based on the provided parameters.
     *
     * @param firstDayOfPreviousMonth First day of the previous month
     * @param income                     Total income for the month
     * @param expenses                    Total expenses for the month
     * @return Monthly statistics DTO
     */
    private static MonthlyStatsDTO getMonthlyStatsDTO(LocalDate firstDayOfPreviousMonth, Double income, Double expenses) {
        // Create a new MonthlyStatsDTO object
        MonthlyStatsDTO monthlyStatsDTO = new MonthlyStatsDTO();

        // Set the total income, handling null values
        monthlyStatsDTO.setTotalIncome(income != null ? BigDecimal.valueOf(income)
                .setScale(2, RoundingMode.HALF_UP).doubleValue() : 0);

        // Set the total expenses, handling null values and taking the absolute value
        monthlyStatsDTO.setTotalExpenses(expenses != null ? BigDecimal.valueOf(Math.abs(expenses))
                .setScale(2, RoundingMode.HALF_UP).doubleValue() : 0);

        // Calculate the net income based on the total income and expenses
        monthlyStatsDTO.setNetIncome(
                BigDecimal.valueOf(monthlyStatsDTO.getTotalIncome() - monthlyStatsDTO.getTotalExpenses())
                        .setScale(2, RoundingMode.HALF_UP).doubleValue());

        // Return the populated MonthlyStatsDTO object
        return monthlyStatsDTO;
    }
}
