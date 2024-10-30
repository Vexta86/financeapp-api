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

        try {
            User currentUser = authService.currentUser(request);
            List<Category> categories = categoryService.getAllCategories(currentUser);
            return ResponseEntity.ok().body(categoryService.convertToDTOS(categories));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Categories not found: " + e.getMessage());
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
}

