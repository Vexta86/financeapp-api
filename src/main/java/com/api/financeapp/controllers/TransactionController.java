package com.api.financeapp.controllers;

import com.api.financeapp.dtos.SingleTransactionDTO;
import com.api.financeapp.entities.*;
import com.api.financeapp.services.AuthService;
import com.api.financeapp.services.CategoryService;
import com.api.financeapp.services.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AuthService authService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Object> getAllTransactions(HttpServletRequest request) {

        User currentUser = authService.currentUser(request);
        if (currentUser != null){
            List<Transaction> transactions = transactionService.getAllTransactions(currentUser);
            return new ResponseEntity<>(transactions, HttpStatus.OK);

        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }

    @GetMapping("/single")
    public ResponseEntity<Object> getSingleTransactions(HttpServletRequest request){
        User currentUser = authService.currentUser(request);
        if (currentUser == null){
            return ResponseEntity.badRequest().body("User not found");
        }

        List<SingleTransaction> transactions = transactionService.getAllSingleTransactions(currentUser);

        return new ResponseEntity<>(transactionService.convertSinglesToDTOS(transactions), HttpStatus.OK);
    }

    @PostMapping("/{categoryId}/single")
    public ResponseEntity<Object> newTransaction(
            @PathVariable Long categoryId,
            @RequestBody SingleTransaction transaction,
            HttpServletRequest request) {


        User currentUser = authService.currentUser(request);
        if (currentUser == null) {
            return ResponseEntity.badRequest().body("User not found");
        }


        Category selectedCategory = categoryService.getByIdAndUser(categoryId, currentUser);
        if (selectedCategory == null){
            return ResponseEntity.badRequest().body("Category not found");
        }

        if (transaction.getAmount() == 0){
            return ResponseEntity.badRequest().body("Amount can't be 0");
        }

        boolean transactionIsExpense = transaction.getAmount() < 0;
        boolean categoryIsExpense = selectedCategory.getType().equals(CategoryType.EXPENSE);

        boolean transactionIsIncomeCatIsExpense = !transactionIsExpense && categoryIsExpense;
        boolean transactionIsExpenseCatIsIncome = transactionIsExpense && !categoryIsExpense;

        if (transactionIsExpenseCatIsIncome || transactionIsIncomeCatIsExpense){
            return ResponseEntity.badRequest().body("Category doesn't match");
        }

        transaction.setCategory(selectedCategory);
        transaction.setUser(currentUser);
        currentUser.changeNetWorth(transaction.getAmount());

        // Create the transaction
        SingleTransaction createdTransaction = transactionService.createSingleTransaction(transaction);

        if (createdTransaction == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transaction could not be created");
        }

        return ResponseEntity.ok(transactionService.convertSingleToDTO(createdTransaction));
    }
}
