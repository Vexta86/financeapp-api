package com.api.financeapp.repositories;

import com.api.financeapp.entities.SharedExpense;
import com.api.financeapp.entities.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SharedExpenseRepository extends JpaRepository<SharedExpense, Long> {

    List<SharedExpense> findByPaidBy(GroupMember member);


    List<SharedExpense> findByPaidBy_ExpenseGroup_Id(Long groupId);

    void deleteByPaidBy_ExpenseGroup_Id(Long groupId);

    @Query("SELECT SUM(se.amount) FROM SharedExpense se " +
            "WHERE se.paidBy.expenseGroup.id = :groupId")
    Float sumByGroupId(@Param("groupId") Long groupId);
}
