package com.api.financeapp.controllers;


import com.api.financeapp.entities.User;
import com.api.financeapp.requests.LoginRequest;
import com.api.financeapp.responses.AuthResponse;
import com.api.financeapp.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody User request){
        return ResponseEntity.ok(service.register(request));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> register(@RequestBody LoginRequest request){
        return ResponseEntity.ok(service.login(request));
    }

}

