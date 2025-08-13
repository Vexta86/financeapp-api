package com.api.financeapp.controllers;

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
    public ResponseEntity<List<String>> getAllCategories(HttpServletRequest request) throws Exception {

        User currentUser = authService.currentUser(request);
        List<String> categories = categoryService.getAllCategories(currentUser);
        return ResponseEntity.ok().body(categories);
    }




}

