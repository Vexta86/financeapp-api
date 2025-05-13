package com.api.financeapp.controllers;

import com.api.financeapp.dtos.ExpenseGroupDTO;
import com.api.financeapp.dtos.GroupMemberDTO;
import com.api.financeapp.dtos.SharedExpenseDTO;
import com.api.financeapp.entities.ExpenseGroup;
import com.api.financeapp.entities.GroupMember;
import com.api.financeapp.entities.SharedExpense;
import com.api.financeapp.entities.User;
import com.api.financeapp.services.AuthService;
import com.api.financeapp.services.ExpenseGroupService;
import com.api.financeapp.services.GroupMemberService;
import com.api.financeapp.services.SharedExpenseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Dictionary;
import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {
    @Setter
    @Getter
    public static class AddMemberRequest {
        private String nickname;
        private String email;
    }
    @Setter
    @Getter
    public static class SolveMemberRequest {
        private Long id;
        private Float percentage;
    }
    private final AuthService authService;

    private final ExpenseGroupService expenseGroupService;
    private final GroupMemberService groupMemberService;

    private final SharedExpenseService sharedExpenseService;

    public GroupController(AuthService authService, ExpenseGroupService expenseGroupService, GroupMemberService groupMemberService, SharedExpenseService sharedExpenseService) {
        this.authService = authService;
        this.expenseGroupService = expenseGroupService;
        this.groupMemberService = groupMemberService;
        this.sharedExpenseService = sharedExpenseService;
    }

    // Group

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseGroupDTO> getGroupById(
            HttpServletRequest request,
            @PathVariable Long id
    ) throws Exception {
        User currentUser = authService.currentUser(request);
        ExpenseGroupDTO group = expenseGroupService.getGroupById(id, currentUser);
        return ResponseEntity.ok(group);
    }
    @GetMapping("/{id}/solve")
    public ResponseEntity<?> solveGroupById(
            HttpServletRequest request,
            @PathVariable Long id
    ) throws Exception {
        User currentUser = authService.currentUser(request);
        Object response = expenseGroupService.solve(id, currentUser);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> deleteGroup(
            @PathVariable Long groupId,
            HttpServletRequest request
    ) throws Exception {
        User currentUser = authService.currentUser(request);

        expenseGroupService.deleteGroup(groupId, currentUser);

        return ResponseEntity.noContent().build(); // HTTP 204
    }

    @PostMapping
    public ResponseEntity<ExpenseGroupDTO> createGroup(
            HttpServletRequest request,
            @RequestParam String name
    ) throws Exception {
        User currentUser = authService.currentUser(request);
        ExpenseGroup group = expenseGroupService.addNewGroup(currentUser, name);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ExpenseGroupDTO(group));
    }
    @GetMapping("/my")
    public ResponseEntity<List<ExpenseGroupDTO>> getMyGroups(HttpServletRequest request) throws Exception {
        User currentUser = authService.currentUser(request);

        List<ExpenseGroup> groups = expenseGroupService.getGroupsByUser(currentUser);

        List<ExpenseGroupDTO> groupDTOs = groups.stream()
                .map(ExpenseGroupDTO::new)
                .toList();

        return ResponseEntity.ok(groupDTOs);
    }


    // Members
    @PostMapping("/{groupId}/members")
    public ResponseEntity<?> addMemberToGroup(
            @PathVariable Long groupId,
            @RequestBody AddMemberRequest memberRequest,
            HttpServletRequest request
    ) throws Exception {
        User currentUser = authService.currentUser(request);

        GroupMember addedMember = groupMemberService.addToGroup(
                currentUser,
                memberRequest.getNickname(),
                memberRequest.getEmail(),
                groupId
        );

        return ResponseEntity.ok(new GroupMemberDTO(addedMember));
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<?> getMembers(
            @PathVariable Long groupId,
            HttpServletRequest request
    ) throws Exception {
        User currentUser = authService.currentUser(request);

        List<GroupMemberDTO> members = groupMemberService.listMembers(groupId, currentUser);

        return ResponseEntity.ok(members);
    }

    @DeleteMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<?> deleteMember(
            @PathVariable Long groupId,
            @PathVariable Long memberId,
            HttpServletRequest request
    ) throws Exception {
        User currentUser = authService.currentUser(request);

        groupMemberService.deleteMember(groupId, memberId, currentUser);

        return ResponseEntity.noContent().build(); // HTTP 204
    }



    // Expenses
    @PostMapping("/{groupId}/expenses")
    public ResponseEntity<SharedExpense> addSharedExpense(
            @PathVariable Long groupId,
            @RequestBody SharedExpense sharedExpense,
            HttpServletRequest request
    ) throws Exception {
        // Retrieve the current user
        User currentUser = authService.currentUser(request);

        // Call the service to add the shared expense
        SharedExpense createdExpense = sharedExpenseService.addSharedExpense(groupId, sharedExpense, currentUser);

        // Return the created expense with HTTP 201 status
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExpense);
    }

    @GetMapping("/{groupId}/expenses")
    public ResponseEntity<List<SharedExpenseDTO>> getExpensesByGroupId(
            @PathVariable Long groupId,
            HttpServletRequest request
    ) throws Exception {
        // Retrieve the current user
        User currentUser = authService.currentUser(request);

        // Fetch the expenses
        List<SharedExpenseDTO> expenses = sharedExpenseService.getExpensesByGroupId(groupId, currentUser);

        // Return the expenses
        return ResponseEntity.ok(expenses);
    }
    @DeleteMapping("/{groupId}/expenses/{expenseId}")
    public ResponseEntity<List<SharedExpenseDTO>> deleteExpenseById(
            @PathVariable Long groupId,
            @PathVariable Long expenseId,
            HttpServletRequest request
    ) throws Exception {
        // Retrieve the current user
        User currentUser = authService.currentUser(request);

        // Fetch the expenses
        sharedExpenseService.deleteExpenseById(groupId, expenseId, currentUser);

        // Return the expenses
        return ResponseEntity.noContent().build(); // HTTP 204
    }
}
