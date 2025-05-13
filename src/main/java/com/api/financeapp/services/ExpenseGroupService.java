package com.api.financeapp.services;

import com.api.financeapp.entities.ExpenseGroup;
import com.api.financeapp.entities.GroupMember;
import com.api.financeapp.entities.User;
import com.api.financeapp.repositories.ExpenseGroupRepository;
import com.api.financeapp.repositories.GroupMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseGroupService {

    private final ExpenseGroupRepository expenseGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserService userService;

    public ExpenseGroupService(ExpenseGroupRepository repository, GroupMemberRepository groupMemberRepository) {
        this.expenseGroupRepository = repository;
        this.groupMemberRepository = groupMemberRepository;
    }

    public ExpenseGroup addNewGroup(User owner, String name){
        ExpenseGroup group = new ExpenseGroup();
        group.setName(name);
        expenseGroupRepository.save(group);

        GroupMember groupMember = new GroupMember();

        groupMember.setExpenseGroup(group);
        groupMember.setNickname(owner.getName());
        groupMember.setPercent(1);
        groupMember.setUser(owner);
        groupMember.setOwner(true);
        groupMemberRepository.save(groupMember);
        return group;
    }
    public ExpenseGroup getGroupById(Long groupId, User requester) throws AccessDeniedException {
        boolean belongs = groupMemberRepository.existsByExpenseGroup_IdAndUser(groupId, requester);
        if (!belongs) {
            throw new AccessDeniedException("You do not belong to this group.");
        }

        return expenseGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));
    }
    public List<ExpenseGroup> getGroupsByUser(User user) {
        return groupMemberRepository.findByUser(user).stream()
                .map(GroupMember::getExpenseGroup)
                .toList();
    }

    public GroupMember addToGroup(User requester, String nickname, String email, Long groupId) throws AccessDeniedException {
        boolean belongs = groupMemberRepository.existsByExpenseGroup_IdAndUser(groupId, requester);
        if (!belongs) {
            throw new AccessDeniedException("You do not belong to this group.");
        }

        ExpenseGroup group = expenseGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));


        GroupMember member = new GroupMember();

        member.setExpenseGroup(group);
        member.setNickname(nickname);

        Optional<User> user = userService.findByEmailAddress(email);

        if (user.isPresent()){
            member.setUser(user.get());
        } else {
            member.setInvitedEmail(email);
        }


        return groupMemberRepository.save(member);
    }
}
