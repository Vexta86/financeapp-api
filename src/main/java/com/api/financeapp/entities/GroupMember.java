package com.api.financeapp.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class GroupMember {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "expense_group_id", nullable = false)
    private ExpenseGroup expenseGroup;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // nullable if the user is not registered

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private float percent;

    @Column(name = "is_owner", nullable = false)
    private boolean isOwner = false;

    @Column(name = "invited_email")
    private String invitedEmail;

    public User getUser() {
        return user;
    }

    public UUID getId() {
        return id;
    }

    public ExpenseGroup getExpenseGroup() {
        return expenseGroup;
    }

    public float getPercent() {
        return percent;
    }

    public String getInvitedEmail() {
        return invitedEmail;
    }

    public String getNickname() {
        return nickname;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setExpenseGroup(ExpenseGroup expenseGroup) {
        this.expenseGroup = expenseGroup;
    }

    public void setInvitedEmail(String invitedEmail) {
        this.invitedEmail = invitedEmail;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }
}
