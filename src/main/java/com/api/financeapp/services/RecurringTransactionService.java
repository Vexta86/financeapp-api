package com.api.financeapp.services;

import com.api.financeapp.dtos.RecurringTransactionDTO;
import com.api.financeapp.entities.*;
import com.api.financeapp.repositories.RecurringTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecurringTransactionService {
    private final RecurringTransactionRepository transactionRepo;
    private final CategoryService categoryService;

    public RecurringTransactionService(RecurringTransactionRepository recurringTransactionRepository, CategoryService categoryService) {
        this.transactionRepo = recurringTransactionRepository;
        this.categoryService = categoryService;
    }

    /**
     * Retrieves a list of all recurring transactions associated with the given user.
     *
     * @param user User entity
     * @return List of recurring transactions
     */
    public List<RecurringTransaction> getAll(User user) {
        // Find all recurring transactions associated with the user
        Optional<List<RecurringTransaction>> transactions = transactionRepo.findAllByUser(user);

        // Return the list of transactions, or an empty list if none are found
        return transactions.orElseGet(ArrayList::new);
    }

    /**
     * Converts a RecurringTransaction entity to a RecurringTransactionDTO.
     *
     * @param transaction RecurringTransaction entity to convert
     * @return RecurringTransactionDTO
     */
    public RecurringTransactionDTO convertToDTO(RecurringTransaction transaction){
        // Return the populated DTO
        return new RecurringTransactionDTO(transaction);
    }

    /**
     * Converts a list of RecurringTransaction entities to a list of RecurringTransactionDTOs.
     *
     * @param transactions List of RecurringTransaction entities
     * @return List of RecurringTransactionDTOs
     */
    public List<RecurringTransactionDTO> convertToDTOS(List<RecurringTransaction> transactions){
        // Use Java streams to map each transaction to a DTO and collect the results
        return transactions
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * Creates a new recurring transaction for the given user.
     *
     * @param transaction    RecurringTransaction entity to create
     * @param currentUser User entity associated with the transaction
     * @return Created SingleTransaction entity
     */
    public RecurringTransaction createTransaction(RecurringTransaction transaction, User currentUser) {
        // Verify that the transaction has a valid category and amount
        if (transaction.getCategory() == null) {
            throw new IllegalArgumentException("Category can't be null");
        }
        if (transaction.getAmount() == 0) {
            throw new IllegalArgumentException("Amount can't be 0");
        }
        if (transaction.getFrequency() <= 0){
            throw new IllegalArgumentException("Frequency can't be less than 0");
        }

        // Set the user and category on the transaction
        transaction.setUser(currentUser);
        transaction.setCategory(transaction.getCategory().trim().toLowerCase());

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
    public RecurringTransaction updateTransaction(Long transactionId, RecurringTransaction updated, User currentUser){

        // Search for the existing transaction in the repository
        Optional<RecurringTransaction> optionalTransaction = transactionRepo
                .findByIdAndUser(transactionId, currentUser);

        // Check if the transaction exists
        if (optionalTransaction.isEmpty()){
            throw new IllegalArgumentException("Transaction not found");
        }

        // Get the existing transaction
        RecurringTransaction existingTransaction = optionalTransaction.get();

        // Update the transaction's description if provided
        if (updated.getDescription() != null){
            existingTransaction.setDescription(updated.getDescription());
        }
        // Update the frequency if provided
        if (updated.getFrequency() > 0){
            existingTransaction.setFrequency(updated.getFrequency());
        }
        // Update the transaction's amount if provided and non-zero
        if (updated.getAmount() != 0){
            existingTransaction.setAmount(updated.getAmount());
        }
        // Update the transaction's category if provided
        if (updated.getCategory() != null){
            existingTransaction.setCategory(updated.getCategory().trim().toLowerCase());
        }
        // Update the frequency unit if provided
        if (updated.getFrequencyUnit() != null){
            existingTransaction.setFrequencyUnit(updated.getFrequencyUnit());
        }

        // Save the updated transaction to the repository
        return transactionRepo.save(existingTransaction);
    }
}
