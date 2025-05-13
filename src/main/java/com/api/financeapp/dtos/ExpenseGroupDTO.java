package com.api.financeapp.dtos;

import com.api.financeapp.entities.ExpenseGroup;

import java.time.LocalDateTime;

public record ExpenseGroupDTO(
        Long id,
        String name,
        LocalDateTime createdAt,
        Float totalAmount
) {
    public ExpenseGroupDTO(ExpenseGroup group) {
        this(group.getId(), group.getName(), group.getCreatedAt(), (float) 0);
    }
    public ExpenseGroupDTO(ExpenseGroup group, Float totalAmount) {
        this(group.getId(), group.getName(), group.getCreatedAt(), totalAmount);
    }
}
