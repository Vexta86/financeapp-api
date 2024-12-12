package com.api.financeapp.services;

import com.api.financeapp.entities.Role;
import com.api.financeapp.entities.User;
import com.api.financeapp.repositories.UserRepository;
import com.api.financeapp.requests.LoginRequest;
import com.api.financeapp.responses.AuthResponse;
import com.api.financeapp.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repo;
    private  PasswordEncoder passwordEncoder;
    private  JwtService jwtService;
    private  AuthenticationManager authenticationManager;
    private UserService userService;
    private final UserRepository userRepository;
    private JavaMailSender javaMailSender;

    @Autowired
    public AuthService(UserRepository repo, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, UserService userService, UserRepository userRepository, JavaMailSender javaMailSender) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.userRepository = userRepository;
        this.javaMailSender = javaMailSender;
    }

    public User register(User request) {
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        request.setRole(Role.USER);
        return repo.save(request);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmailAddress(), request.getPassword()));
        User user = repo.findByEmailAddress(request.getEmailAddress()).orElseThrow();
        String token = jwtService.generateToken(user);
        if (!user.isActive()){
            throw new IllegalArgumentException("Please activate your account");
        }
        return AuthResponse.builder().token(token).build();
    }

    public User currentUser(HttpServletRequest request) throws Exception {
        String userEmail = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null;
        if (userEmail == null) {
            throw new Exception("Invalid JWT signature");
        }
        Optional<User> currentUser = userService.findByEmailAddress(userEmail);
        if (currentUser.isEmpty()){
            throw new Exception("Invalid JWT signature");
        }
        return currentUser.get();
    }

    public String generateVerificationCode(String email){
        // Generate a 6-digit random code
        String code = RandomStringUtils.randomNumeric(6);
        // Store the code in the database along with the user's email
        Optional<User> userOptional = userRepository.findByEmailAddress(email);
        if (userOptional.isEmpty()){
            throw new IllegalArgumentException("User not found");
        }

        User user = userOptional.get();
        Instant now = Instant.now();
        if (user.getVerificationCodeTimestamp() != null){
            Instant expirationTime = user.getVerificationCodeTimestamp().plusSeconds(300);
            if (now.isBefore(expirationTime)){
                throw new IllegalArgumentException("There is a valid code in your email");

            }
        }
        user.setVerificationCode(code);
        user.setVerificationCodeTimestamp(now);
        userRepository.save(user);
        return code;
    }

    public void sendVerificationEmail(String email, String code){
        Optional<User> userOptional = userRepository.findByEmailAddress(email);
        if (userOptional.isEmpty()){
            throw new IllegalArgumentException("User not found");
        }

        User user = userOptional.get();
        if (user.isActive()){
            throw new IllegalArgumentException("User already verified");
        }


        // Email the user with the verification code
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verify your email address");
        message.setText("Your verification code is: " + code);
        javaMailSender.send(message);
    }

    public boolean verifyCode(String email, String code){
        // Verify the code by checking if it matches the one stored in the database
        Optional<User> userOptional = userRepository.findByEmailAddress(email);
        if (userOptional.isEmpty()){
            throw new IllegalArgumentException("User not found");
        }
        User user = userOptional.get();
        Instant now = Instant.now();
        // 5 minutes
        Instant expirationTime = user.getVerificationCodeTimestamp().plusSeconds(300);
        if (now.isAfter(expirationTime)){
            throw new IllegalArgumentException("Code is expired");
        }
        // Verify if the code matches
        if (!user.getVerificationCode().equals(code)){
            throw new IllegalArgumentException("Invalid code");
        }
        return true;
    }

    public void activateAccount(String email){
        // Activate the user's account by updating their status in the database
        Optional<User> userOptional = userRepository.findByEmailAddress(email);
        if (userOptional.isEmpty()){
            throw new IllegalArgumentException("User not found");
        }
        User user = userOptional.get();
        user.setActive(true);
        userRepository.save(user);
    }

}
