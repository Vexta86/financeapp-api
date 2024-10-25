package com.api.financeapp.services;

import com.api.financeapp.dtos.CategoryDTO;
import com.api.financeapp.entities.Category;
import com.api.financeapp.entities.User;
import com.api.financeapp.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

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

    public Category createCategory(Category category) {
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

}
