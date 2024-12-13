package com.api.financeapp.controllers;

import com.api.financeapp.entities.RecurringTransaction;
import com.api.financeapp.entities.User;
import com.api.financeapp.services.AuthService;
import com.api.financeapp.services.RecurringTransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions/recurring")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RecurringTransactionController {
    private final RecurringTransactionService transactionService;
    private final AuthService authService;

    public RecurringTransactionController(RecurringTransactionService transactionService, AuthService authService) {
        this.transactionService = transactionService;
        this.authService = authService;
    }

    /**
     * Retrieves a list of single transactions for the current user.
     *
     * @param request HttpServletRequest
     * @return ResponseEntity with a list of single transactions
     */
    @GetMapping
    public ResponseEntity<Object> getSingleTransactions(HttpServletRequest request){
        try{
            // Get the current user from the request
            User currentUser = authService.currentUser(request);

            // Retrieve the list of single transactions for the current user
            List<RecurringTransaction> transactions = transactionService.getAll(currentUser);

            // Convert the transactions to DTOs and return them in the response
            return ResponseEntity.status(HttpStatus.OK).body(transactionService.convertToDTOS(transactions));
        }catch (Exception e){
            // Return an error response if an exception occurs
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Transactions not found: " + e.getMessage());
        }
    }

    /**
     * Deletes a transaction by its ID.
     *
     * @param transactionId ID of the transaction to delete
     * @param request        HttpServletRequest
     * @return ResponseEntity with a success message
     */
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Object> deleteTransaction(@PathVariable Long transactionId, HttpServletRequest request){
        try {
            // Get the current user from the request
            User currentUser = authService.currentUser(request);

            // Delete the transaction with the given ID
            transactionService.deleteTransaction(transactionId, currentUser);

            // Return a success response
            return ResponseEntity.status(HttpStatus.OK).body("Transaction deleted");
        } catch (Exception e){
            // Return an error response if an exception occurs
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Transaction could not be deleted: " + e.getMessage());
        }
    }

    /**
     * Creates a new recurring transaction.
     *
     * @param transaction RecurringTransaction entity to create
     * @param request      HttpServletRequest
     * @return ResponseEntity with the created transaction
     */
    @PostMapping()
    public ResponseEntity<Object> newTransaction(
            @RequestBody RecurringTransaction transaction,
            HttpServletRequest request) {

        try {
            // Get the current user from the request
            User currentUser = authService.currentUser(request);

            // Create the new transaction
            RecurringTransaction createdTransaction = transactionService.createTransaction(transaction, currentUser);

            // Return the created transaction in the response
            return ResponseEntity.ok(transactionService.convertToDTO(createdTransaction));
        } catch (Exception e) {
            // Return an error response if an exception occurs
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Transaction could not be created: " + e.getMessage());
        }
    }

    /**
     * Updates an existing recurring transaction.
     *
     * @param id        ID of the transaction to update
     * @param updated   RecurringTransaction entity with updated data
     * @param request   HttpServletRequest
     * @return ResponseEntity with the updated transaction
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchRecurringTransaction(
            @PathVariable Long id,
            @RequestBody RecurringTransaction updated,
            HttpServletRequest request){
        try {
            // Get the current user from the request
            User currentUser = authService.currentUser(request);

            // Update the transaction with the given ID
            RecurringTransaction updatedTransaction = transactionService.updateTransaction(id, updated, currentUser);

            // Return the updated transaction in the response
            return ResponseEntity.status(HttpStatus.OK).body(transactionService.convertToDTO(updatedTransaction));
        } catch (Exception e){
            // Return an error response if an exception occurs
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Transaction could not be updated: " + e.getMessage());
        }
    }

}
