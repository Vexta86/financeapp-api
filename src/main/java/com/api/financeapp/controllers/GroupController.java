package com.api.financeapp.controllers;

import com.api.financeapp.dtos.ExpenseGroupDTO;
import com.api.financeapp.entities.ExpenseGroup;
import com.api.financeapp.entities.User;
import com.api.financeapp.services.AuthService;
import com.api.financeapp.services.ExpenseGroupService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {
    private final AuthService authService;

    private final ExpenseGroupService expenseGroupService;

    public GroupController(AuthService authService, ExpenseGroupService expenseGroupService) {
        this.authService = authService;
        this.expenseGroupService = expenseGroupService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseGroupDTO> getGroupById(
            HttpServletRequest request,
            @PathVariable Long id
    ) throws Exception {
        User currentUser = authService.currentUser(request);
        ExpenseGroup group = expenseGroupService.getGroupById(id, currentUser);
        return ResponseEntity.ok(new ExpenseGroupDTO(group));
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



}
