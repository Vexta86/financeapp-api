package com.api.financeapp.services;


import com.api.financeapp.entities.Role;
import com.api.financeapp.entities.User;
import com.api.financeapp.repositories.UserRepository;
import com.api.financeapp.requests.LoginRequest;
import com.api.financeapp.responses.AuthResponse;
import com.api.financeapp.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    public AuthResponse register(User request) {
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        request.setRole(Role.USER);
        repo.save(request);
        var jwtToken = jwtService.generateToken(request);
        return AuthResponse.builder().token(jwtToken).build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmailAddress(), request.getPassword()));
        User user = repo.findByEmailAddress(request.getEmailAddress()).orElseThrow();
        String token = jwtService.generateToken(user);

        return AuthResponse.builder().token(token).build();
    }

    public User currentUser(HttpServletRequest request){
        String userEmail = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null;
        if (userEmail == null) {
            return null;
        }
        Optional<User> currentUser = userService.findByEmailAddress(userEmail);
        return currentUser.orElse(null);
    }
}
