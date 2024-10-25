package com.api.financeapp.controllers;

import com.api.financeapp.entities.Category;
import com.api.financeapp.entities.User;
import com.api.financeapp.services.AuthService;
import com.api.financeapp.services.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    @Autowired
    private AuthService authService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Object> getAllCategories(HttpServletRequest request){
        User currentUser = authService.currentUser(request);
        if (currentUser == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        List<Category> categories = categoryService.getAllCategories(currentUser);

        return ResponseEntity.ok().body(categoryService.convertToDTOS(categories));
    }



    @PostMapping
    public ResponseEntity<Object> newCategory(@RequestBody Category newCategory, HttpServletRequest request){
        System.out.println("received category: " + newCategory);
        User currentUser = authService.currentUser(request);
        if (currentUser != null) {
            // Set the user on the transaction
            newCategory.setUser(currentUser);
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }

        if (categoryService.checkIfExists(newCategory, currentUser)){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Conflict: Category could not be created due to an existing category or similar issue.");

        }

        Category createdCategory = categoryService.createCategory(newCategory);

        return ResponseEntity.ok(categoryService.convertToDTO(createdCategory));

    }
}

