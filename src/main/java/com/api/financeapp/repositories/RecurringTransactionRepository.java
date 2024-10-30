package com.api.financeapp.repositories;

import com.api.financeapp.entities.RecurringTransaction;
import com.api.financeapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecurringTransactionRepository extends JpaRepository<RecurringTransaction, Long> {
    Optional<List<RecurringTransaction>> findAllByUser(User user);
}
