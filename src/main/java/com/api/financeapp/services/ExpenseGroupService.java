package com.api.financeapp.services;

import com.api.financeapp.controllers.GroupController;
import com.api.financeapp.dtos.ExpenseGroupDTO;
import com.api.financeapp.dtos.GroupMemberDTO;
import com.api.financeapp.entities.ExpenseGroup;
import com.api.financeapp.entities.GroupMember;
import com.api.financeapp.entities.SharedExpense;
import com.api.financeapp.entities.User;
import com.api.financeapp.repositories.ExpenseGroupRepository;
import com.api.financeapp.repositories.GroupMemberRepository;
import com.api.financeapp.repositories.SharedExpenseRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class ExpenseGroupService {

    private final ExpenseGroupRepository expenseGroupRepository;
    private final GroupMemberRepository groupMemberRepository;

    private final SharedExpenseRepository sharedExpenseRepository;

    public ExpenseGroupService(ExpenseGroupRepository repository, GroupMemberRepository groupMemberRepository, SharedExpenseRepository sharedExpenseRepository) {
        this.expenseGroupRepository = repository;
        this.groupMemberRepository = groupMemberRepository;
        this.sharedExpenseRepository = sharedExpenseRepository;
    }

    /**
     * Creates a new expense group and assigns the owner as a group member.
     *
     * @param owner The user who will own the group.
     * @param name  The name of the group.
     * @return The created ExpenseGroup entity.
     */
    public ExpenseGroup addNewGroup(User owner, String name) {
        // Create a new expense group
        ExpenseGroup group = new ExpenseGroup();
        group.setName(name);
        expenseGroupRepository.save(group);

        // Create a new group member for the owner
        GroupMember groupMember = new GroupMember();
        groupMember.setExpenseGroup(group);
        groupMember.setNickname(owner.getName());
        groupMember.setUser(owner);
        groupMember.setOwner(true);
        groupMemberRepository.save(groupMember);

        return group;
    }

    /**
     * Retrieves an expense group by its ID if the requester belongs to the group.
     *
     * @param groupId   The ID of the group to retrieve.
     * @param requester The user making the request.
     * @return The ExpenseGroup entity.
     * @throws AccessDeniedException If the requester does not belong to the group.
     */
    public ExpenseGroupDTO getGroupById(Long groupId, User requester) throws AccessDeniedException {
        // Check if the requester belongs to the group
        boolean belongs = groupMemberRepository.existsByExpenseGroup_IdAndUser(groupId, requester);
        if (!belongs) {
            throw new AccessDeniedException("You do not belong to this group.");
        }

        // Retrieve the group by its ID
        ExpenseGroup group = expenseGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));

        // Retrieve the total spent in the group
        Float total = sharedExpenseRepository.sumByGroupId(groupId);

        return new  ExpenseGroupDTO(group, total);
    }

    /**
     * Retrieves a list of expense groups that the user belongs to.
     *
     * @param user The user whose groups are to be retrieved.
     * @return A list of ExpenseGroup entities.
     */
    public List<ExpenseGroup> getGroupsByUser(User user) {
        // Find all groups the user belongs to
        return groupMemberRepository.findByUser(user).stream()
                .map(GroupMember::getExpenseGroup)
                .toList();
    }




    /**
     * Deletes a group if the requester is the owner of the group.
     *
     * @param groupId   The ID of the group to be deleted.
     * @param requester The user making the request.
     * @throws AccessDeniedException If the requester does not belong to the group or is not the owner.
     */
    @Transactional
    public void deleteGroup(Long groupId, User requester) throws AccessDeniedException {
        // Verify that the requester belongs to the group
        GroupMember member = groupMemberRepository.findByExpenseGroup_IdAndUser(groupId, requester).orElseThrow(
                () -> new AccessDeniedException("You do not belong to this group.")
        );

        // Check if the requester is the owner of the group
        if (!member.isOwner()) {
            // Throw an exception if the requester is not the owner
            throw new AccessDeniedException("You are not the owner of this group.");
        }

        // Delete all shared expenses associated with the group
        sharedExpenseRepository.deleteByPaidBy_ExpenseGroup_Id(groupId);

        // Delete all group members associated with the group
        groupMemberRepository.deleteByExpenseGroup_Id(groupId);

        // Delete the group itself
        expenseGroupRepository.deleteById(groupId);
    }


    @Getter
    @Setter
    public static class GroupMemberSolver {
        private Long memberId;
        private Float balance;
        private String nickname;
        public GroupMemberSolver(GroupMember groupMember, Float balance) {
            this.memberId = groupMember.getId();
            this.nickname = groupMember.getNickname();
            this.balance = balance;
        }
    }

    @Getter
    @Setter
    public static class DebtTransaction {
        private String from;
        private String to;
        private Float amount;
    }
    public Object solve(Long groupId, User requester) {
        // Check if the requester belongs to the group
        boolean belongs = groupMemberRepository.existsByExpenseGroup_IdAndUser(groupId, requester);
        if (!belongs) {
            throw new AccessDeniedException("You do not belong to this group.");
        }

        List<GroupMember> members = groupMemberRepository.findByExpenseGroup_Id(groupId);
        List<SharedExpense> expenses = sharedExpenseRepository.findByPaidBy_ExpenseGroup_Id(groupId);

        Map<Long, GroupMemberSolver> memberSolvers = new HashMap<>();
        Float total = sharedExpenseRepository.sumByGroupId(groupId);
        float totalPerMember = total / members.size();

        // Initialize balances
        for (GroupMember member : members) {
            memberSolvers.put(member.getId(), new GroupMemberSolver(member, -totalPerMember));
        }

        // Adjust balances based on expenses
        for (SharedExpense expense : expenses) {
            GroupMemberSolver solver = memberSolvers.get(expense.getPaidBy().getId());
            if (solver != null) {
                solver.setBalance(solver.getBalance() + expense.getAmount());
            }
        }

        // Prepare list of balances and corresponding member info
        List<Float> netAmounts = new ArrayList<>();
        List<GroupMemberSolver> orderedMembers = new ArrayList<>();
        for (GroupMemberSolver solver : memberSolvers.values()) {
            if (Math.abs(solver.getBalance()) > 0.01f) {
                netAmounts.add(solver.getBalance());
                orderedMembers.add(solver);
            }
        }

        List<DebtTransaction> transactions = new ArrayList<>();
        minimizeTransactions(netAmounts, orderedMembers, transactions);
        return transactions;
    }

    private void minimizeTransactions(List<Float> netAmounts, List<GroupMemberSolver> members, List<DebtTransaction> transactions) {
        int maxCredit = 0, maxDebit = 0;
        for (int i = 1; i < netAmounts.size(); i++) {
            if (netAmounts.get(i) > netAmounts.get(maxCredit)) maxCredit = i;
            if (netAmounts.get(i) < netAmounts.get(maxDebit)) maxDebit = i;
        }

        // Base case
        if (Math.abs(netAmounts.get(maxCredit)) < 0.01f && Math.abs(netAmounts.get(maxDebit)) < 0.01f) {
            return;
        }

        float amount = Math.min(-netAmounts.get(maxDebit), netAmounts.get(maxCredit));
        netAmounts.set(maxCredit, netAmounts.get(maxCredit) - amount);
        netAmounts.set(maxDebit, netAmounts.get(maxDebit) + amount);

        DebtTransaction transaction = new DebtTransaction();
        transaction.setFrom(members.get(maxDebit).getNickname());
        transaction.setTo(members.get(maxCredit).getNickname());
        transaction.setAmount(amount);
        transactions.add(transaction);

        // Recursive call
        minimizeTransactions(netAmounts, members, transactions);
    }




}
