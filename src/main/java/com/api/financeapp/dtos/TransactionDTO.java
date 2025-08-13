package com.api.financeapp.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransactionDTO {
    private Long id;
    private Double amount;
    private String description;
    private String category;
}
