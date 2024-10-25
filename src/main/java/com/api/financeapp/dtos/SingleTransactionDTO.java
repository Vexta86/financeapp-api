package com.api.financeapp.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SingleTransactionDTO {
    private Long amount;
    private String description;
    private String date;
    private String categoryName;
}
