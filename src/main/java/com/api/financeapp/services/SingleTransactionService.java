package com.api.financeapp.services;

import com.api.financeapp.dtos.CategoryDTO;
import com.api.financeapp.dtos.SingleTransactionDTO;
import com.api.financeapp.entities.*;
import com.api.financeapp.repositories.SingleTransactionRepository;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SingleTransactionService {

    private final SingleTransactionRepository transactionRepo;
    private final CategoryService categoryService;

    public SingleTransactionService(
            CategoryService categoryService,
            SingleTransactionRepository singleTransactionRepository
    ) {
        this.categoryService = categoryService;
        this.transactionRepo = singleTransactionRepository;
    }

    /**
     * Retrieves a list of single transactions for a user within a specified date range.
     *
     * @param user User entity
     * @param start Start date of the range
     * @param end   End date of the range
     * @return List of single transactions
     */
    public List<SingleTransaction> getAllByDate(User user, LocalDate start, LocalDate end) {
        // Retrieve transactions from the repository
        Optional<List<SingleTransaction>> transactions = transactionRepo
                .findAllByUserAndDateBetweenOrderByDateDesc(user, start, end);

        // Return the transactions, or an empty list if none are found
        return transactions.orElseGet(ArrayList::new);
    }

    /**
     * Retrieves a list of single transactions for a user within a specified date range,
     * using string representations of the dates.
     *
     * @param user        User entity
     * @param startString Start date of the range, in YYYY-MM-DD format
     * @param endString   End date of the range, in YYYY-MM-DD format
     * @return List of single transactions
     */
    public List<SingleTransaction> getAllByStringDate(User user, String startString, String endString) {
        LocalDate start;
        LocalDate end;

        // If start or end date is not provided, use the current month
        if (startString == null || endString == null) {
            LocalDate currentDate = LocalDate.now();
            start = currentDate.with(TemporalAdjusters.firstDayOfMonth());
            end = currentDate.with(TemporalAdjusters.lastDayOfMonth());
        } else {
            // Parse the start and end dates from the string representations
            start = LocalDate.parse(startString);
            end = LocalDate.parse(endString);
        }

        // Delegate to the getAllByDate method to retrieve the transactions
        return getAllByDate(user, start, end);
    }

    /**
     * Calculates the net worth of a user by summing up all transaction amounts.
     *
     * @param user User entity
     * @return Net worth of the user
     */
    public Double getNetWorth(User user) {
        // Retrieve the total amount of all transactions for the user from the repository
        Double totalAmount = transactionRepo.sumAmountByUser(user);

        // Return the total amount, or 0 if it's null

        return totalAmount != null ?
                BigDecimal.valueOf(totalAmount).setScale(2, RoundingMode.HALF_UP).doubleValue() :
                0f;
    }

    /**
     * Converts a SingleTransaction entity to a SingleTransactionDTO.
     *
     * @param transaction SingleTransaction entity
     * @return SingleTransactionDTO
     */
    public SingleTransactionDTO convertToDTO(SingleTransaction transaction){
        // Return the populated DTO
        return new SingleTransactionDTO(transaction);
    }

    /**
     * Converts a list of SingleTransaction entities to a list of SingleTransactionDTOs.
     *
     * @param transactions List of SingleTransaction entities
     * @return List of SingleTransactionDTOs
     */
    public List<SingleTransactionDTO> convertToDTOS(List<SingleTransaction> transactions){
        // Use Java streams to map each transaction to a DTO and collect the results
        return transactions
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * Creates a new single transaction for the given user.
     *
     * @param transaction    SingleTransaction entity to create
     * @param currentUser User entity associated with the transaction
     * @return Created SingleTransaction entity
     */
    public SingleTransaction createTransaction(SingleTransaction transaction, User currentUser) {
        // Verify that the transaction has a valid category and amount
        if (transaction.getCategory() == null) {
            throw new IllegalArgumentException("Category can't be null");
        }
        if (transaction.getAmount() == 0) {
            throw new IllegalArgumentException("Amount can't be 0");
        }

        // Identifies the chosen category
        Category selectedCategory = categoryService.selectedSingleCategory(transaction, currentUser);

        // Set the user and category on the transaction
        transaction.setUser(currentUser);
        transaction.setCategory(selectedCategory);

        // Save the transaction to the repository
        return transactionRepo.save(transaction);
    }

    /**
     * Deletes a transaction by its ID, ensuring that the transaction belongs to the current user.
     *
     * @param transactionId ID of the transaction to delete
     * @param currentUser    User entity associated with the transaction
     */
    @Transactional
    public void deleteTransaction(Long transactionId, User currentUser) {

        // Check if the transaction exists and belongs to the current user
        if (!transactionRepo.existsByIdAndUser(transactionId, currentUser)){
            throw new IllegalArgumentException("Transaction not found");
        }

        // Delete the transaction from the repository
        transactionRepo.deleteById(transactionId);
    }

    /**
     * Updates an existing transaction with new data, ensuring that the transaction belongs to the current user.
     *
     * @param transactionId ID of the transaction to update
     * @param updated        Updated transaction data
     * @param currentUser    User entity associated with the transaction
     * @return Updated transaction entity
     */
    public SingleTransaction updateTransaction(Long transactionId, SingleTransaction updated, User currentUser){

        // Search for the existing transaction in the repository
        Optional<SingleTransaction> optionalTransaction = transactionRepo
                .findByIdAndUser(transactionId, currentUser);

        // Check if the transaction exists
        if (optionalTransaction.isEmpty()){
            throw new IllegalArgumentException("Transaction not found");
        }

        // Get the existing transaction
        SingleTransaction existingTransaction = optionalTransaction.get();

        // Update the transaction's description if provided
        if (updated.getDescription() != null){
            existingTransaction.setDescription(updated.getDescription());
        }

        // Update the transaction's amount if provided and non-zero
        if (updated.getAmount() != 0){
            existingTransaction.setAmount(updated.getAmount());
        }

        // Update the transaction's category if provided
        if (updated.getCategory() != null){


            // Find or create the category
            Category selectedCategory = categoryService.selectedSingleCategory(updated, currentUser);

            // Check if the category type matches the transaction type
            if (existingTransaction.getAmount() < 0 && selectedCategory.getType().equals(CategoryType.INCOME)){
                throw new IllegalArgumentException("Category type doesn't match");
            }
            if (existingTransaction.getAmount() > 0 && selectedCategory.getType().equals(CategoryType.EXPENSE)){
                throw new IllegalArgumentException("Category type doesn't match");
            }
            existingTransaction.setCategory(selectedCategory);
        }

        // Update the transaction's date if provided
        if (updated.getDate() != null){
            existingTransaction.setDate(updated.getDate());
        }

        // Save the updated transaction to the repository
        return transactionRepo.save(existingTransaction);
    }


}
