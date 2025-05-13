package com.api.financeapp.dtos;

import com.api.financeapp.entities.GroupMember;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SharedExpenseDTO(
        String id,
        Float amount,
        String description,
        LocalDateTime createdAt,
        GroupMemberDTO paidBy
) {
    public SharedExpenseDTO(com.api.financeapp.entities.SharedExpense sharedExpense) {
        this(
                sharedExpense.getId().toString(),
                sharedExpense.getAmount(),
                sharedExpense.getDescription(),
                sharedExpense.getCreatedAt(),
                new GroupMemberDTO(sharedExpense.getPaidBy())
        );
    }
}