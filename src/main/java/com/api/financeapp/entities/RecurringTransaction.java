package com.api.financeapp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;


@Getter
@Entity
public class RecurringTransaction{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private Double amount;


    @Setter
    @Column(nullable = false)
    private String description;

    @Setter
    @ManyToOne
    @JoinColumn()
    private Category category;

    @Setter
    @ManyToOne
    @JoinColumn()
    private User user;

    @Setter
    private Integer frequency;

    @Setter
    @Enumerated(EnumType.STRING)
    private FrequencyUnit frequencyUnit;

    private Float daysToMonths(Float days){
        return (float) Math.round((days / 29.6) * 100) / 100;
    }

    public Float getMonthlyFrequency(){
        if (frequencyUnit.equals(FrequencyUnit.MONTHS)){
            return Float.valueOf(frequency);
        }
        if (frequencyUnit.equals(FrequencyUnit.DAYS)){
            return daysToMonths(Float.valueOf(frequency));
        }
        if (frequencyUnit.equals(FrequencyUnit.WEEKS)){
            return daysToMonths((float) (frequency * 7));
        }
        // if frequency is years
        return (float) (frequency * 12);
    }
    public Double getMonthlyBudget(){
        return Double.parseDouble(String.format(Locale.US, "%.2f", this.getAmount() / this.getMonthlyFrequency()));
    }

}
