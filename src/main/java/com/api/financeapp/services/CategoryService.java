package com.api.financeapp.services;

import com.api.financeapp.dtos.CategoryDTO;
import com.api.financeapp.entities.*;
import com.api.financeapp.repositories.CategoryRepository;
import com.api.financeapp.repositories.RecurringTransactionRepository;
import com.api.financeapp.repositories.SingleTransactionRepository;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SingleTransactionRepository singleTransactionRepository;


    public CategoryService(CategoryRepository categoryRepository, SingleTransactionRepository singleTransactionRepository) {
        this.categoryRepository = categoryRepository;
        this.singleTransactionRepository = singleTransactionRepository;
    }

    /**
     * Converts a Category entity to a CategoryDTO.
     *
     * @param category Category entity to convert
     * @return CategoryDTO
     */
    public CategoryDTO convertToDTO(Category category){
        // Return the populated DTO
        return new CategoryDTO(category);
    }

    /**
     * Converts a list of Category entities to a list of CategoryDTOs.
     *
     * @param categories List of Category entities to convert
     * @return List of CategoryDTOs
     */
    public List<CategoryDTO> convertToDTOS(List<Category> categories){
        // Use Java streams to map each category to a DTO and collect the results
        return categories
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    /**
     * Retrieves a list of categories associated with the given user.
     *
     * @param user User entity
     * @return List of categories
     */
    public List<String> getAllCategories(User user) {
        // Return the list of categories, or an empty list if none are found
        return singleTransactionRepository.findDistinctCategoriesByUser(user);
    }


    /**
     * Finds a category by its ID or name, or saves a new category if it doesn't exist.
     *
     * @param category Category entity to find or save
     * @return Found or saved category entity
     */
    public Category findOrSaveCategory(Category category){
        // Check if the category with the given ID exists in the repository
        if (category.getId() != null){
            // Find the category by its ID and user
            Optional<Category> existingCategory = categoryRepository.findByIdAndUser(category.getId(), category.getUser());

            // If the category is found, verify that its type matches the given category's type
            if (existingCategory.isPresent()){
                if (existingCategory.get().getType() != category.getType()){
                    throw new IllegalArgumentException("Category type doesn't match");
                }
                // Return the existing category
                return existingCategory.get();
            }
        }

        // If the category is not found by ID, check if it exists by name
        if (category.getName() != null){
            // Find the category by its name, type, and user
            Optional<Category> existingCategory = categoryRepository
                    .findByNameAndTypeAndUser(category.getName(), category.getType(), category.getUser());

            // If the category is found, return it; otherwise, save the given category
            return existingCategory.orElseGet(() -> categoryRepository.save(category));
        }

        // If the category is not found by ID or name, throw an exception
        throw new IllegalArgumentException("Category doesn't exist neither could be created");
    }


    /**
     * Selects a recurring transaction category, determining its type based on the transaction amount.
     *
     * @param transaction RecurringTransaction entity
     * @param currentUser  User entity associated with the transaction
     * @return Selected Category entity
     */
    public Category selectedRecurringCategory(RecurringTransaction transaction, User currentUser){
        // Determine the type of transaction (income or expense) based on the amount
        CategoryType transactionType = (transaction.getAmount() < 0) ? CategoryType.EXPENSE : CategoryType.INCOME;

        // Set the user and type on the transaction's category
        transaction.getCategory().setUser(currentUser);
        transaction.getCategory().setType(transactionType);

        // Find or create the category
        return findOrSaveCategory(transaction.getCategory());
    }



}
