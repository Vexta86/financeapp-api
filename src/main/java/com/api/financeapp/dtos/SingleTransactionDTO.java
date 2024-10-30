package com.api.financeapp.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class SingleTransactionDTO extends TransactionDTO{
    private Date date;
}
