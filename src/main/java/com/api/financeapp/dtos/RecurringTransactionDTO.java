package com.api.financeapp.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RecurringTransactionDTO extends TransactionDTO{
    private Integer frequency;
    private String frequencyUnit;
    private Float monthlyBudget;
}
