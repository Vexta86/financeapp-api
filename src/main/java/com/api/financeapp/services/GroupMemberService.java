package com.api.financeapp.services;

import com.api.financeapp.dtos.GroupMemberDTO;
import com.api.financeapp.entities.ExpenseGroup;
import com.api.financeapp.entities.GroupMember;
import com.api.financeapp.entities.User;
import com.api.financeapp.repositories.ExpenseGroupRepository;
import com.api.financeapp.repositories.GroupMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupMemberService {
    private final ExpenseGroupRepository expenseGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserService userService;

    public GroupMemberService(ExpenseGroupRepository expenseGroupRepository, GroupMemberRepository groupMemberRepository, UserService userService) {
        this.expenseGroupRepository = expenseGroupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userService = userService;
    }

    /**
     * Adds a new member to an expense group if the requester belongs to the group.
     *
     * @param requester The user making the request.
     * @param nickname  The nickname of the new member.
     * @param email     The email of the new member.
     * @param groupId   The ID of the group to add the member to.
     * @return The created GroupMember entity.
     * @throws AccessDeniedException If the requester does not belong to the group.
     * @throws IllegalArgumentException If the nickname or email is already in use in the group.
     */
    public GroupMember addToGroup(User requester, String nickname, String email, Long groupId) throws AccessDeniedException {
        // Check if the requester belongs to the group
        boolean belongs = groupMemberRepository.existsByExpenseGroup_IdAndUser(groupId, requester);
        if (!belongs) {
            throw new AccessDeniedException("You do not belong to this group.");
        }

        // Validate that the nickname is not already in use in the group
        boolean nicknameIsInGroup = groupMemberRepository.existsByExpenseGroup_IdAndNickname(groupId, nickname);
        if (nicknameIsInGroup) {
            throw new IllegalArgumentException("Nickname already in use");
        }

        // Validate that the email is not already in use in the group
        boolean emailIsInGroup = groupMemberRepository.existsByExpenseGroup_IdAndInvitedEmail(groupId, nickname);
        if (emailIsInGroup) {
            throw new IllegalArgumentException("Email already in the group");
        }

        // Retrieve the group by its ID
        ExpenseGroup group = expenseGroupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));

        // Create a new group member
        GroupMember member = new GroupMember();
        member.setExpenseGroup(group);

        member.setNickname(nickname);
        // Check if the user exists by email
        Optional<User> user = userService.findByEmailAddress(email);
        if (user.isPresent()) {
            // Validate that the user is not already in the group
            boolean userIsInGroup = groupMemberRepository.existsByExpenseGroup_IdAndUser(groupId, user.get());
            if (userIsInGroup) {
                throw new IllegalArgumentException("User already in group");
            }
            member.setUser(user.get());
        } else {
            // Set the invited email if the user does not exist
            member.setInvitedEmail(email);
        }

        return groupMemberRepository.save(member);
    }

    /**
     * Retrieves a list of group members for a specific group and maps them to DTOs.
     *
     * @param groupId   The ID of the group whose members are to be retrieved.
     * @param requester The user making the request.
     * @return A list of GroupMemberDto objects representing the group members.
     * @throws AccessDeniedException If the requester does not belong to the group.
     */
    public List<GroupMemberDTO> listMembers(Long groupId, User requester) throws AccessDeniedException {
        // Check if the requester belongs to the group
        boolean belongs = groupMemberRepository.existsByExpenseGroup_IdAndUser(groupId, requester);
        if (!belongs) {
            // Throw an exception if the requester is not a member of the group
            throw new AccessDeniedException("You do not belong to this group.");
        }

        // Retrieve the group members and map them to DTOs
        return groupMemberRepository.findByExpenseGroup_Id(groupId).stream()
                .map(GroupMemberDTO::new) // Convert each GroupMember entity to a GroupMemberDto
                .toList();
    }
    /**
     * Deletes a member from a group if the requester is a member of the group and the member to be deleted is not the owner.
     *
     * @param groupId   The ID of the group from which the member is to be deleted.
     * @param memberId  The ID of the member to be deleted.
     * @param requester The user making the request.
     * @throws AccessDeniedException If the requester does not belong to the group, the member cannot be found,
     *                               or the member to be deleted is the owner of the group.
     */
    @Transactional
    public void deleteMember(Long groupId, Long memberId, User requester) throws AccessDeniedException {
        // Check if the requester belongs to the group
        boolean belongs = groupMemberRepository.existsByExpenseGroup_IdAndUser(groupId, requester);
        if (!belongs) {
            // Throw an exception if the requester is not a member of the group
            throw new AccessDeniedException("You do not belong to this group.");
        }

        // Retrieve the member to be deleted by their ID
        GroupMember member = groupMemberRepository.findById(memberId).orElseThrow(
                () -> new AccessDeniedException("Can't find that member.")
        );

        // Check if the member to delete is the owner of the group
        if (member.isOwner()) {
            // Throw an exception if the member is the owner
            throw new AccessDeniedException("You can't delete the owner of the group.");
        }

        // Delete the member from the group
        groupMemberRepository.deleteById(memberId);
    }
}
