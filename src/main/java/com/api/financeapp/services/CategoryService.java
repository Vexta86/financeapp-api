package com.api.financeapp.services;

import com.api.financeapp.dtos.CategoryDTO;
import com.api.financeapp.entities.Category;
import com.api.financeapp.entities.SingleTransaction;
import com.api.financeapp.entities.Transaction;
import com.api.financeapp.entities.User;
import com.api.financeapp.repositories.CategoryRepository;
import com.api.financeapp.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    public CategoryDTO convertToDTO(Category category){
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setType(category.getType());
        return dto;
    }
    public List<CategoryDTO> convertToDTOS(List<Category> categories){

        return categories
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public List<Category> getAllCategories(User user) {
        Optional<List<Category>> categories =  categoryRepository.findAllByUser(user);
        return categories.orElseGet(ArrayList::new);
    }

    public Category createCategory(Category category, User user) {

        boolean exists = categoryRepository.existsByNameAndTypeAndUser(category.getName(), category.getType(), user);
        if (exists){
            throw new IllegalArgumentException("Category already exists");
        }
        category.setUser(user);
        return categoryRepository.save(category);
    }

    public boolean checkIfExists(Category category, User user){
        Optional<Category> categoryInDb = categoryRepository.findAllByNameAndTypeAndUser(category.getName(), category.getType(), user);
        return categoryInDb.isPresent();
    }

    public Category getByIdAndUser(Long id, User user){
        try {
            Optional<Category> category = categoryRepository.findByIdAndUser(id, user);
            return category.orElseGet(null);
        }catch (Exception e){
            return null;
        }


    }

    @Transactional
    public void deleteCategory(Long categoryId, User currentUser) {
        Optional<Category> category = categoryRepository.findByIdAndUser(categoryId, currentUser);
        if (category.isEmpty()){
            throw new IllegalArgumentException("Category not found");
        }
        List<Transaction> transactionsInCategory = transactionRepository.findAllByCategoryAndUser(category.get(), currentUser);
        transactionsInCategory.forEach(transaction -> {
            if (transaction instanceof SingleTransaction){
                currentUser.changeNetWorth(-1*transaction.getAmount());
            }
            transactionRepository.deleteByIdAndUser(transaction.getId(), currentUser);

        });
        categoryRepository.deleteById(categoryId);
    }
}
