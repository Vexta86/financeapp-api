package com.api.financeapp.entities;

import jakarta.persistence.*;

@Entity
public class RecurringTransaction extends Transaction{
    private Float frequency;
    private Float monthlyFrequency;

    @Enumerated(EnumType.STRING)

    private FrequencyUnit frequencyUnit;

}
