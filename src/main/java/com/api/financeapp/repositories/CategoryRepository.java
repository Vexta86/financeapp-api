package com.api.financeapp.repositories;

import com.api.financeapp.entities.Category;
import com.api.financeapp.entities.CategoryType;
import com.api.financeapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<List<Category>> findAllByUser(User user);

    Optional<Category> findByIdAndUser(Long id, User user);

    Optional<Category> findAllByNameAndTypeAndUser(String name, CategoryType type, User user);


}
