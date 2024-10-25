package com.api.financeapp.repositories;

import com.api.financeapp.entities.SingleTransaction;
import com.api.financeapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SingleTransactionRepository extends JpaRepository<SingleTransaction, Long> {
    Optional<List<SingleTransaction>> findAllByUser(User user);

}
