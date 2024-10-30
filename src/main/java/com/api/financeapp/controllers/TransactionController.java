package com.api.financeapp.controllers;

import com.api.financeapp.entities.*;
import com.api.financeapp.services.AuthService;
import com.api.financeapp.services.CategoryService;
import com.api.financeapp.services.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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
        try {
            User currentUser = authService.currentUser(request);
            List<Transaction> transactions = transactionService.getAllTransactions(currentUser);
            return new ResponseEntity<>(transactions, HttpStatus.OK);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Transactions not found: " + e.getMessage());
        }


    }

    @GetMapping("/single")
    public ResponseEntity<Object> getSingleTransactions(HttpServletRequest request){
        try{
            User currentUser = authService.currentUser(request);
            List<SingleTransaction> transactions = transactionService.getAllSingleTransactions(currentUser);
            return ResponseEntity.status(HttpStatus.OK).body(transactionService.convertSingleToDTOS(transactions));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Transactions not found: " + e.getMessage());
        }
    }

    @GetMapping("/recurring")
    public ResponseEntity<Object> getRecurrentTransaction(HttpServletRequest request){
        try {
            User currentUser = authService.currentUser(request);
            List<RecurringTransaction> transactions = transactionService.getAllRecurringTransactions(currentUser);
            return ResponseEntity.status(HttpStatus.OK).body(transactionService.convertRecurringToDTOS(transactions));
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Transactions not found: " + e.getMessage());

        }
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Object> deleteTransaction(@PathVariable Long transactionId, HttpServletRequest request){
        try {
            User currentUser = authService.currentUser(request);
            transactionService.deleteTransaction(transactionId, currentUser);
            return ResponseEntity.status(HttpStatus.OK).body("Transaction deleted");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Transaction could not be deleted: " + e.getMessage());
        }
    }

    @PostMapping("/{categoryId}/recurring")
    public ResponseEntity<Object> newRecurringTransaction(
            @PathVariable Long categoryId,
            @RequestBody RecurringTransaction transaction,
            HttpServletRequest request
    ) {
        try {
            User currentUser = authService.currentUser(request);
            Transaction createdTransaction = transactionService.createTransaction(categoryId, transaction, currentUser);
            return ResponseEntity.status(HttpStatus.OK).body(transactionService.convertToDto(createdTransaction));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Transaction could not be created: " + e.getMessage());
        }
    }

    @PostMapping("/{categoryId}/single")
    public ResponseEntity<Object> newTransaction(
            @PathVariable Long categoryId,
            @RequestBody SingleTransaction transaction,
            HttpServletRequest request) {
        
        try {
            User currentUser = authService.currentUser(request);
            Transaction createdTransaction = transactionService.createTransaction(categoryId, transaction, currentUser);
            return ResponseEntity.ok(transactionService.convertToDto(createdTransaction));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Transaction could not be created: " + e.getMessage());
        }
    }
}
