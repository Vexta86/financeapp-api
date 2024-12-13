package com.api.financeapp.controllers;

import com.api.financeapp.entities.Category;
import com.api.financeapp.entities.RecurringTransaction;
import com.api.financeapp.entities.SingleTransaction;
import com.api.financeapp.entities.User;
import com.api.financeapp.services.AuthService;
import com.api.financeapp.services.CategoryService;
import com.api.financeapp.services.RecurringTransactionService;
import com.api.financeapp.services.SingleTransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    private final AuthService authService;

    private final CategoryService categoryService;
    private final SingleTransactionService singleTransactionService;
    private final RecurringTransactionService recurringTransactionService;

    public CategoryController(AuthService authService, CategoryService categoryService, SingleTransactionService singleTransactionService, RecurringTransactionService recurringTransactionService) {
        this.authService = authService;
        this.categoryService = categoryService;
        this.singleTransactionService = singleTransactionService;
        this.recurringTransactionService = recurringTransactionService;
    }

    @GetMapping
    public ResponseEntity<Object> getAllCategories(HttpServletRequest request){

        try {
            User currentUser = authService.currentUser(request);
            List<Category> categories = categoryService.getAllCategories(currentUser);
            return ResponseEntity.ok().body(categoryService.convertToDTOS(categories));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Categories not found: " + e.getMessage());
        }
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Object> getCategoryItems(@PathVariable Long categoryId, HttpServletRequest request){
        try {
            User currentUser = authService.currentUser(request);
            List<SingleTransaction> transactions = categoryService.getSingles(categoryId, currentUser);
            return ResponseEntity.status(HttpStatus.OK).body(singleTransactionService.convertToDTOS(transactions));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Category not found: " + e.getMessage());
        }
    }

    @GetMapping("/recurring/{categoryId}")
    public ResponseEntity<Object> getCategoryRecurring(@PathVariable Long categoryId, HttpServletRequest request){
        try {
            User currentUser = authService.currentUser(request);
            List<RecurringTransaction> transactions = categoryService.getRecurring(categoryId, currentUser);
            return ResponseEntity.status(HttpStatus.OK).body(recurringTransactionService.convertToDTOS(transactions));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Category not found: " + e.getMessage());
        }
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Object> deleteCategory(@PathVariable Long categoryId, HttpServletRequest request){
        try {
            User currentUser = authService.currentUser(request);
            categoryService.deleteCategory(categoryId, currentUser);
            return ResponseEntity.status(HttpStatus.OK).body("Category deleted");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Category could not be deleted: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> newCategory(@RequestBody Category newCategory, HttpServletRequest request){

        try{
            User currentUser = authService.currentUser(request);
            Category createdCategory = categoryService.createCategory(newCategory, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.convertToDTO(createdCategory));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Category could not be created: " + e.getMessage());
        }

    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<Object> updateCategory(@PathVariable Long categoryId, @RequestBody Category updated, HttpServletRequest request){
        try {
            User currentUser = authService.currentUser(request);
            Category category = categoryService.updateCategory(categoryId, updated, currentUser);
            return ResponseEntity.status(HttpStatus.OK).body(categoryService.convertToDTO(category));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Category could not be updated: " + e.getMessage());

        }
    }


}

