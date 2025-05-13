package com.api.financeapp.services;

import com.api.financeapp.dtos.SharedExpenseDTO;
import com.api.financeapp.entities.GroupMember;
import com.api.financeapp.entities.SharedExpense;
import com.api.financeapp.entities.User;
import com.api.financeapp.repositories.GroupMemberRepository;
import com.api.financeapp.repositories.SharedExpenseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SharedExpenseService {
    private final SharedExpenseRepository sharedExpenseRepository;
    private final GroupMemberRepository groupMemberRepository;

    public SharedExpenseService(SharedExpenseRepository sharedExpenseRepository, GroupMemberRepository groupMemberRepository) {
        this.sharedExpenseRepository = sharedExpenseRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    public SharedExpense addSharedExpense(Long groupId, SharedExpense sharedExpense, User requester) {

        // Check the group of the shared expense
        Long memberId = sharedExpense.getPaidBy().getId();
        if (memberId == null) {
            throw new IllegalArgumentException("PaidBy cannot be null");
        }
        GroupMember member = groupMemberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("PaidBy not found"));

        if (!member.getExpenseGroup().getId().equals(groupId)) {
            throw new AccessDeniedException("PaidBy does not belong to the group");
        }

        // Check if the requester belongs to the same group as the member
        boolean belongs = groupMemberRepository.existsByExpenseGroup_IdAndUser(groupId, requester);
        if (!belongs) {
            throw new AccessDeniedException("You do not belong to this group.");
        }


        return sharedExpenseRepository.save(sharedExpense);
    }
    public List<SharedExpenseDTO> getExpensesByGroupId(Long groupId, User requester) {
        // Check if the requester belongs to the group
        boolean belongs = groupMemberRepository.existsByExpenseGroup_IdAndUser(groupId, requester);
        if (!belongs) {
            throw new AccessDeniedException("You do not belong to this group.");
        }

        // Fetch and return the expenses
        return sharedExpenseRepository.findByPaidBy_ExpenseGroup_Id(groupId).stream().map(SharedExpenseDTO::new).toList();
    }
    @Transactional
    public void deleteExpenseById(Long groupId, Long expenseId, User requester) {
        // Check if the requester belongs to the group
        boolean belongs = groupMemberRepository.existsByExpenseGroup_IdAndUser(groupId, requester);
        if (!belongs) {
            throw new AccessDeniedException("You do not belong to this group.");
        }

        SharedExpense expense = sharedExpenseRepository.findById(expenseId)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found"));

        // Check if the expense belongs to the group
        if (!expense.getPaidBy().getExpenseGroup().getId().equals(groupId)) {
            throw new AccessDeniedException("Expense does not belong to this group");
        }


        // Fetch and return the expenses
         sharedExpenseRepository.delete(expense);
    }
}
