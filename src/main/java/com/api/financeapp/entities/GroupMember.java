package com.api.financeapp.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(
        name = "group_member",
        uniqueConstraints = @UniqueConstraint(columnNames = {"expense_group_id", "user_id"})
)
public class GroupMember {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "expense_group_id", nullable = false)
    private ExpenseGroup expenseGroup;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // nullable if the user is not registered

    @Column(nullable = false)
    private String nickname;

    @Column()
    private float percent;

    @Column(name = "is_owner", nullable = false)
    private boolean isOwner = false;

    @Column(name = "invited_email")
    private String invitedEmail;


}
