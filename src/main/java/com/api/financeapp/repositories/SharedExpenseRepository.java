package com.api.financeapp.repositories;

import com.api.financeapp.entities.SharedExpense;
import com.api.financeapp.entities.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SharedExpenseRepository extends JpaRepository<SharedExpense, UUID> {

    List<SharedExpense> findByPaidBy(GroupMember member);


}
