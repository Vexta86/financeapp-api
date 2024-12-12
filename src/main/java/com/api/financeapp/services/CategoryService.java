package com.api.financeapp.services;

import com.api.financeapp.dtos.CategoryDTO;
import com.api.financeapp.entities.*;
import com.api.financeapp.repositories.CategoryRepository;
import com.api.financeapp.repositories.RecurringTransactionRepository;
import com.api.financeapp.repositories.SingleTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final SingleTransactionRepository singleTransactionRepository;
    private final RecurringTransactionRepository recurringTransactionRepository;

    public CategoryService(CategoryRepository categoryRepository, SingleTransactionRepository singleTransactionRepository, RecurringTransactionRepository recurringTransactionRepository) {
        this.categoryRepository = categoryRepository;
        this.singleTransactionRepository = singleTransactionRepository;
        this.recurringTransactionRepository = recurringTransactionRepository;
    }

    /**
     * Converts a Category entity to a CategoryDTO.
     *
     * @param category Category entity to convert
     * @return CategoryDTO
     */
    public CategoryDTO convertToDTO(Category category){
        // Create a new CategoryDTO
        CategoryDTO dto = new CategoryDTO();

        // Populate the DTO with data from the category entity
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setType(category.getType());

        // Return the populated DTO
        return dto;
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
    public List<Category> getAllCategories(User user) {
        // Retrieve the list of categories from the repository
        Optional<List<Category>> categories =  categoryRepository.findAllByUser(user);

        // Return the list of categories, or an empty list if none are found
        return categories.orElseGet(ArrayList::new);
    }

    /**
     * Creates a new category for the given user.
     *
     * @param category Category entity to create
     * @param user      User entity associated with the category
     * @return Created category entity
     */
    public Category createCategory(Category category, User user) {

        // Check if a category with the same name, type, and user already exists
        boolean exists = categoryRepository.existsByNameAndTypeAndUser(category.getName(), category.getType(), user);

        // If the category already exists, throw an exception
        if (exists){
            throw new IllegalArgumentException("Category already exists");
        }

        // Set the user on the category
        category.setUser(user);

        // Save the category to the repository
        return categoryRepository.save(category);
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
     * Selects a single transaction category, determining its type based on the transaction amount.
     *
     * @param transaction SingleTransaction entity
     * @param currentUser  User entity associated with the transaction
     * @return Selected Category entity
     */
    public Category selectedSingleCategory(SingleTransaction transaction, User currentUser){
        // Determine the type of transaction (income or expense) based on the amount
        CategoryType transactionType = (transaction.getAmount() < 0) ? CategoryType.EXPENSE : CategoryType.INCOME;

        // Set the user and type on the transaction's category
        transaction.getCategory().setUser(currentUser);
        transaction.getCategory().setType(transactionType);

        // Find or create the category
        return findOrSaveCategory(transaction.getCategory());
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

    /**
     * Deletes a category and all its associated transactions.
     *
     * @param categoryId ID of the category to delete
     * @param currentUser User entity associated with the category
     */
    @Transactional
    public void deleteCategory(Long categoryId, User currentUser) {
        // Find the category by its ID and user
        Optional<Category> category = categoryRepository.findByIdAndUser(categoryId, currentUser);

        // Check if the category exists
        if (category.isEmpty()){
            throw new IllegalArgumentException("Category not found");
        }

        // Delete all single transactions associated with the category and user
        singleTransactionRepository.deleteAllByCategoryAndUser(category.get(), currentUser);

        // Delete all recurring transactions associated with the category and user
        recurringTransactionRepository.deleteAllByCategoryAndUser(category.get(), currentUser);

        // Delete the category
        categoryRepository.deleteById(categoryId);
    }

    /**
     * Retrieves a list of single transactions associated with a category and user.
     *
     * @param categoryId ID of the category
     * @param currentUser User entity associated with the category
     * @return List of single transactions
     */
    public List<SingleTransaction> getSingles(Long categoryId, User currentUser) {
        // Find the category by its ID and user
        Optional<Category> selectedCategory = categoryRepository.findByIdAndUser(categoryId, currentUser);

        // Check if the category exists
        if (selectedCategory.isEmpty()){
            throw new IllegalArgumentException("Category not found");
        }

        // Find all single transactions associated with the category and user
        Optional<List<SingleTransaction>> transactions = singleTransactionRepository
                .findAllByUserAndCategory(currentUser, selectedCategory.get());

        // Return the list of transactions, or an empty list if none are found
        return transactions.orElseGet(ArrayList::new);
    }

    /**
     * Retrieves a list of recurring transactions associated with a category and user.
     *
     * @param categoryId ID of the category
     * @param currentUser User entity associated with the category
     * @return List of recurring transactions
     */
    public List<RecurringTransaction> getRecurring(Long categoryId, User currentUser) {
        // Find the category by its ID and user
        Optional<Category> selectedCategory = categoryRepository.findByIdAndUser(categoryId, currentUser);

        // Check if the category exists
        if (selectedCategory.isEmpty()){
            throw new IllegalArgumentException("Category not found");
        }

        // Find all recurring transactions associated with the category and user
        Optional<List<RecurringTransaction>> transactions = recurringTransactionRepository.findAllByUserAndCategory(currentUser, selectedCategory.get());

        // Return the list of transactions, or an empty list if none are found
        return transactions.orElseGet(ArrayList::new);
    }

    /**
     * Updates an existing category with new data.
     *
     * @param categoryId ID of the category to update
     * @param updated     Updated category data
     * @param user        User entity associated with the category
     * @return Updated category entity
     */
    public Category updateCategory(Long categoryId, Category updated, User user){
        // Find the category by its ID and user
        Optional<Category> optionalCategory = categoryRepository.findByIdAndUser(categoryId, user);

        // Check if the category exists
        if (optionalCategory.isEmpty()){
            throw new IllegalArgumentException("Category not found");
        }

        // Get the existing category
        Category category = optionalCategory.get();

        // Update the category's name if provided
        if (updated.getName() != null){
            // Check if the new name is different from the existing name
            boolean isDifferentName = !updated.getName().equals(category.getName());

            // Check if a category with the same name, type, and user already exists
            boolean exists = categoryRepository.existsByNameAndTypeAndUser(updated.getName(), category.getType(), user);

            // If the category already exists and the name is different, throw an exception
            if (exists && isDifferentName){
                throw new IllegalArgumentException("Category already exists");
            }

            // Update the category's name
            category.setName(updated.getName());
        }

        // Save the updated category to the repository
        return categoryRepository.save(category);
    }
}
