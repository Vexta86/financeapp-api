package com.api.financeapp.repositories;

import com.api.financeapp.entities.Category;
import com.api.financeapp.entities.CategoryType;
import com.api.financeapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(exported = false)
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<List<Category>> findAllByUser(User user);

    boolean existsByNameAndTypeAndUser(String name, CategoryType type, User user);
    boolean existsByIdAndUser(Long id, User user);
    Optional<Category> findByIdAndUser(Long id, User user);
    Optional<Category> findByNameAndTypeAndUser(String name, CategoryType type, User user);

    Optional<Category> findAllByNameAndTypeAndUser(String name, CategoryType type, User user);


}
