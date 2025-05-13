package com.api.financeapp.dtos;

import com.api.financeapp.entities.GroupMember;

public record GroupMemberDTO(
        Long id,
        String nickname,
        String email,
        boolean isOwner
) {
    public GroupMemberDTO(GroupMember groupMember) {
        this(
                groupMember.getId(),
                groupMember.getNickname(),
                groupMember.getUser() != null ? groupMember.getUser().getEmailAddress() : groupMember.getInvitedEmail(),
                groupMember.isOwner()
        );
    }
}