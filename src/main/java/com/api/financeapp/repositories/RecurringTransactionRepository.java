package com.api.financeapp.repositories;

import com.api.financeapp.entities.Category;
import com.api.financeapp.entities.RecurringTransaction;

import com.api.financeapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;


@RepositoryRestResource(exported = false)
public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {
    Optional<List<RecurringTransaction>> findAllByUser(User user);

    List<RecurringTransaction> findAllByCategoryAndUser(Category category, User user);
    boolean existsByIdAndUser(Long id, User user);
    Optional<RecurringTransaction> findByIdAndUser(Long id, User user);
    void deleteAllByCategoryAndUser(Category category, User user);


    Optional<List<RecurringTransaction>> findAllByUserAndCategory(User user, Category category);
}
