package com.api.financeapp.controllers;

import com.api.financeapp.dtos.StatsDTO;
import com.api.financeapp.entities.User;
import com.api.financeapp.services.AuthService;
import com.api.financeapp.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;
    @GetMapping
    public ResponseEntity<Object> getSelf(HttpServletRequest request){
        try {
            User currentUser = authService.currentUser(request);

            return ResponseEntity.status(HttpStatus.OK).body(userService.convertUserToDto(currentUser));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Forbidden: " + e.getMessage());
        }
    }
    @GetMapping("/stats")
    public ResponseEntity<Object> getStats(
            HttpServletRequest request,
            @RequestParam int previousMonths
    ){
        try{
            User currentUser = authService.currentUser(request);

            StatsDTO statsDTO = userService.getStats(currentUser, previousMonths);
            return ResponseEntity.status(HttpStatus.OK).body(statsDTO);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Transactions not found: " + e.getMessage());
        }
    }

    @GetMapping("/this-month")
    public ResponseEntity<Object> thisMonth(
            HttpServletRequest request,
            @RequestParam String currentDate
    ){
        try{
            User currentUser = authService.currentUser(request);

            Object statsDTO = userService.getStatsThisMonth(currentUser, currentDate);
            return ResponseEntity.status(HttpStatus.OK).body(statsDTO);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Transactions not found: " + e.getMessage());
        }
    }
}
