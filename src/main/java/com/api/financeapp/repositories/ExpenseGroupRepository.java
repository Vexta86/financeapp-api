package com.api.financeapp.repositories;

import com.api.financeapp.entities.ExpenseGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExpenseGroupRepository extends JpaRepository<ExpenseGroup, Long> {

    // Add custom queries if needed
}
