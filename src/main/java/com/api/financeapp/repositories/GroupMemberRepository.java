package com.api.financeapp.repositories;

import com.api.financeapp.entities.GroupMember;
import com.api.financeapp.entities.ExpenseGroup;
import com.api.financeapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    boolean existsByExpenseGroup_IdAndUser(Long groupId, User user);
    List<GroupMember> findByExpenseGroup(ExpenseGroup group);
    List<GroupMember> findByExpenseGroup_Id(Long groupId);

    List<GroupMember> findByUser(User user);

    Optional<GroupMember> findByExpenseGroupAndUser(ExpenseGroup group, User user);

    boolean existsByExpenseGroup_IdAndNickname(Long groupId, String nickname);

    boolean existsByExpenseGroup_IdAndInvitedEmail(Long groupId, String email);

    Optional<GroupMember> findByExpenseGroup_IdAndUser(Long groupId, User user);

    void deleteByExpenseGroup_Id(Long groupId);
}
