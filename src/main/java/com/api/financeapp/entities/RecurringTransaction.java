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

    public String getDescription() {
        return description;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public Double getAmount() {
        return amount;
    }

    public FrequencyUnit getFrequencyUnit() {
        return frequencyUnit;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public void setFrequencyUnit(FrequencyUnit frequencyUnit) {
        this.frequencyUnit = frequencyUnit;
    }

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
