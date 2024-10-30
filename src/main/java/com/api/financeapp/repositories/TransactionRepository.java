package com.api.financeapp.repositories;

import com.api.financeapp.entities.Category;
import com.api.financeapp.entities.Transaction;
import com.api.financeapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<List<Transaction>> findAllByUser(User user);

    List<Transaction> findAllByCategoryAndUser(Category category, User user);
    boolean existsByIdAndUser(Long id, User user);

    void deleteByIdAndUser(Long id, User user);
}
