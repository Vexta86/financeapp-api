package com.api.financeapp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Entity
public class RecurringTransaction extends Transaction{
    private Float daysToMonths(Float days){

        return (float) Math.round((days / 29.6) * 100) / 100;

    }

    private Float monthlyFrequency;

    private Float monthlyBudget;

    @Setter
    private Integer frequency;

    @Setter
    @Enumerated(EnumType.STRING)
    private FrequencyUnit frequencyUnit;

    public void updateMonthlyFrequency(){
        if (frequencyUnit.equals(FrequencyUnit.MONTHS)){
            this.monthlyFrequency = Float.valueOf(frequency);
        }
        if (frequencyUnit.equals(FrequencyUnit.DAYS)){
            this.monthlyFrequency = daysToMonths(Float.valueOf(frequency));
        }
        if (frequencyUnit.equals(FrequencyUnit.WEEKS)){
            this.monthlyFrequency = daysToMonths((float) (frequency * 7));
        }
        if (frequencyUnit.equals(FrequencyUnit.YEARS)){
            this.monthlyFrequency = (float) (frequency * 12);
        }
        monthlyBudget = (float) Math.round((this.getAmount() / this.monthlyFrequency)*100) / 100;
    }



}
