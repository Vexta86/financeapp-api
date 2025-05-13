package com.api.financeapp.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedExpense {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private float amount;

    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "paid_by_group_member", nullable = false)
    private GroupMember paidBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public float getAmount() {
        return amount;
    }

    public GroupMember getPaidBy() {
        return paidBy;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setPaidBy(GroupMember paidBy) {
        this.paidBy = paidBy;
    }

}
