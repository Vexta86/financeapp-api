package com.api.financeapp.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class SingleTransactionDTO extends TransactionDTO{
    private LocalDate date;
}
