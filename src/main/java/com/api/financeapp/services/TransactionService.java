package com.api.financeapp.services;

import com.api.financeapp.dtos.RecurringTransactionDTO;
import com.api.financeapp.dtos.SingleTransactionDTO;
import com.api.financeapp.entities.*;
import com.api.financeapp.repositories.RecurringTransactionRepository;
import com.api.financeapp.repositories.SingleTransactionRepository;
import com.api.financeapp.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SingleTransactionRepository singleTransactionRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RecurringTransactionRepository recurringTransactionRepository;

    public List<Transaction> getAllTransactions(User user) {
        Optional<List<Transaction>> transactions = transactionRepository.findAllByUser(user);
        return transactions.orElseGet(ArrayList::new);

    }

    private SingleTransactionDTO convertSingleToDTO(SingleTransaction transaction){
        SingleTransactionDTO dto = new SingleTransactionDTO();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setDescription(transaction.getDescription());
        dto.setCategoryName(transaction.getCategory().getName());
        dto.setCategoryId(transaction.getCategory().getId());

        dto.setDate(transaction.getDate());
        return dto;
    }
    private RecurringTransactionDTO convertRecurringToDTO(RecurringTransaction transaction){
        RecurringTransactionDTO dto = new RecurringTransactionDTO();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setDescription(transaction.getDescription());
        dto.setCategoryName(transaction.getCategory().getName());
        dto.setCategoryId(transaction.getCategory().getId());

        dto.setFrequency(transaction.getFrequency());
        dto.setFrequencyUnit(String.valueOf(transaction.getFrequencyUnit()));
        dto.setMonthlyBudget(transaction.getMonthlyBudget());
        return dto;
    }
    public Object convertToDto(Object transaction){
        if (transaction instanceof SingleTransaction){
            return convertSingleToDTO((SingleTransaction) transaction);
        } else {
            return convertRecurringToDTO((RecurringTransaction) transaction);
        }
    }
    public List<SingleTransactionDTO> convertSingleToDTOS(List<SingleTransaction> transactions){
        return transactions
                .stream()
                .map(this::convertSingleToDTO)
                .toList();
    }
    public List<RecurringTransactionDTO> convertRecurringToDTOS(List<RecurringTransaction> transactions){
        return transactions
                .stream()
                .map(this::convertRecurringToDTO)
                .toList();
    }

    private Category findCategory(Long categoryId, Long transactionAmount, User currentUser){
        Category selectedCategory = categoryService.getByIdAndUser(categoryId, currentUser);
        if (selectedCategory == null) {
            throw new IllegalArgumentException("Category not found");
        }

        boolean transactionIsExpense = transactionAmount < 0;
        boolean categoryIsExpense = selectedCategory.getType().equals(CategoryType.EXPENSE);

        boolean transactionIsIncomeCatIsExpense = !transactionIsExpense && categoryIsExpense;
        boolean transactionIsExpenseCatIsIncome = transactionIsExpense && !categoryIsExpense;

        if (transactionIsExpenseCatIsIncome || transactionIsIncomeCatIsExpense) {
            throw new IllegalArgumentException("Category doesn't match");
        }
        return selectedCategory;
    }
    public Transaction createTransaction(Long categoryId, Transaction transaction, User currentUser) {

        if (transaction.getAmount() == 0) {
            throw new IllegalArgumentException("Amount can't be 0");
        }

        Category selectedCategory = findCategory(categoryId, transaction.getAmount(), currentUser);

        transaction.setUser(currentUser);
        transaction.setCategory(selectedCategory);

        if (transaction instanceof RecurringTransaction){
            ((RecurringTransaction) transaction).updateMonthlyFrequency();
        }

        if (transaction instanceof SingleTransaction){
            currentUser.changeNetWorth(transaction.getAmount());
        }

        // Create the transaction
        return transactionRepository.save(transaction);
    }


    public List<SingleTransaction> getAllSingleTransactions(User user) {
        Optional<List<SingleTransaction>> transactions = singleTransactionRepository.findAllByUser(user);
        return transactions.orElseGet(ArrayList::new);

    }
    public List<RecurringTransaction> getAllRecurringTransactions(User user) {
        Optional<List<RecurringTransaction>> transactions = recurringTransactionRepository.findAllByUser(user);
        return transactions.orElseGet(ArrayList::new);
    }

    @Transactional
    public void deleteTransaction(Long transactionId, User currentUser) {

        if (!transactionRepository.existsByIdAndUser(transactionId, currentUser)){
            throw new IllegalArgumentException("Transaction not found");
        }
        Optional<Transaction> deletedTransaction = transactionRepository.findById(transactionId);

        if (deletedTransaction.isPresent()) {
            Transaction transaction = deletedTransaction.get();
            if (transaction instanceof SingleTransaction) {
                currentUser.changeNetWorth(-1*transaction.getAmount());
            }
        }

        transactionRepository.deleteById(transactionId);
    }
}
