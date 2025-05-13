package com.api.financeapp.dtos;

import com.api.financeapp.entities.SingleTransaction;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


import java.time.LocalDate;

public record SingleTransactionDTO(
        Long id,
        Double amount,
        String description,
        CategoryDTO category,
        LocalDate date
) {
    public SingleTransactionDTO(SingleTransaction entity) {
        this(
                entity.getId(),
                entity.getAmount(),
                entity.getDescription(),
                new CategoryDTO(entity.getCategory()),
                entity.getDate()
        );
    }

}

