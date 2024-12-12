package com.api.financeapp.controllers;

import com.api.financeapp.entities.*;
import com.api.financeapp.services.AuthService;
import com.api.financeapp.services.SingleTransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class SingleTransactionController {
    private final SingleTransactionService transactionService;
    private final AuthService authService;

    public SingleTransactionController(SingleTransactionService transactionService, AuthService authService) {
        this.transactionService = transactionService;
        this.authService = authService;
    }

    /**
     * Retrieves a list of single transactions for the current user,
     * filtered by optional start and end dates.
     *
     * @param request  HttpServletRequest
     * @param startDate Optional start date filter (format: yyyy-MM-dd)
     * @param endDate   Optional end date filter (format: yyyy-MM-dd)
     * @return ResponseEntity with a list of single transactions
     */
    @GetMapping
    public ResponseEntity<Object> getSingleTransactions(
            HttpServletRequest request,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ){
        try{
            // Get the current user from the request
            User currentUser = authService.currentUser(request);

            // Retrieve the list of single transactions for the current user,
            // filtered by the optional start and end dates
            List<SingleTransaction> transactions = transactionService.getAllByStringDate(currentUser, startDate, endDate);

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
     * Creates a new single transaction.
     *
     * @param transaction SingleTransaction entity to create
     * @param request      HttpServletRequest
     * @return ResponseEntity with the created transaction
     */
    @PostMapping()
    public ResponseEntity<Object> newTransaction(
            @RequestBody SingleTransaction transaction,
            HttpServletRequest request) {

        try {
            // Get the current user from the request
            User currentUser = authService.currentUser(request);

            // Create the new transaction
            SingleTransaction createdTransaction = transactionService.createTransaction(transaction, currentUser);

            // Return the created transaction in the response
            return ResponseEntity.ok(transactionService.convertToDTO(createdTransaction));
        } catch (Exception e) {
            // Return an error response if an exception occurs
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Transaction could not be created: " + e.getMessage());
        }
    }

    /**
     * Updates an existing single transaction.
     *
     * @param id        ID of the transaction to update
     * @param updated   SingleTransaction entity with updated data
     * @param request   HttpServletRequest
     * @return ResponseEntity with the updated transaction
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchRecurringTransaction(
            @PathVariable Long id,
            @RequestBody SingleTransaction updated,
            HttpServletRequest request){
        try {
            // Get the current user from the request
            User currentUser = authService.currentUser(request);

            // Update the transaction with the given ID
            SingleTransaction updatedTransaction = transactionService.updateTransaction(id, updated, currentUser);

            // Return the updated transaction in the response
            return ResponseEntity.status(HttpStatus.OK).body(transactionService.convertToDTO(updatedTransaction));
        } catch (Exception e){
            // Return an error response if an exception occurs
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Transaction could not be updated: " + e.getMessage());
        }
    }

}