package com.api.financeapp.services;

import com.api.financeapp.entities.*;
import com.api.financeapp.repositories.SingleTransactionRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final SingleTransactionRepository singleTransactionRepository;


    public CategoryService(SingleTransactionRepository singleTransactionRepository) {
        this.singleTransactionRepository = singleTransactionRepository;
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


}
