package com.api.financeapp.repositories;

import com.api.financeapp.entities.ExpenseGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExpenseGroupRepository extends JpaRepository<ExpenseGroup, UUID> {
    // Add custom queries if needed
}
