package com.api.financeapp.dtos;
import com.api.financeapp.entities.FrequencyUnit;
import com.api.financeapp.entities.RecurringTransaction;

public record RecurringTransactionDTO(
        Long id,
        Double amount,
        String description,
        CategoryDTO category,
        Integer frequency,
        FrequencyUnit frequencyUnit,
        Double monthlyBudget
) {
    public RecurringTransactionDTO(RecurringTransaction entity) {
        this(
                entity.getId(),
                entity.getAmount(),
                entity.getDescription(),
                new CategoryDTO(entity.getCategory()),
                entity.getFrequency(),
                entity.getFrequencyUnit(),
                entity.getMonthlyBudget()
        );
    }
}
