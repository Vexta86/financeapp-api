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

    /**
     * Registers a new user.
     *
     * @param request The user registration request.
     * @return The registered user.
     */
    public User register(User request) {
        // Hash the user's password before saving it to the database
        request.setPassword(passwordEncoder.encode(request.getPassword()));

        // Set the user's role to USER by default
        request.setRole(Role.USER);

        // Save the user to the database
        return repo.save(request);
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param request The user login request.
     * @return The authentication response containing the JWT token.
     */
    public AuthResponse login(LoginRequest request) {
        // Authenticate the user using the authentication manager
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmailAddress(), request.getPassword()));

        // Retrieve the user from the database
        User user = repo.findByEmailAddress(request.getEmailAddress()).orElseThrow();

        // Generate a JWT token for the user
        String token = jwtService.generateToken(user);

        // Check if the user's account is active
        if (!user.isActive()){
            // Throw an exception if the account is not active
            throw new IllegalArgumentException("Please activate your account");
        }

        // Return the authentication response with the JWT token
        return AuthResponse.builder().token(token).build();
    }

    /**
     * Retrieves the current user based on the JWT token in the request.
     *
     * @param request The HTTP request containing the JWT token.
     * @return The current user.
     * @throws Exception If the JWT token is invalid or the user is not found.
     */
    public User currentUser(HttpServletRequest request) throws Exception {
        // Get the user's email from the JWT token
        String userEmail = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null;

        // Check if the user's email is null
        if (userEmail == null) {
            // Throw an exception if the JWT token is invalid
            throw new Exception("Invalid JWT signature");
        }

        // Find the user by email
        Optional<User> currentUser = userService.findByEmailAddress(userEmail);

        // Check if the user is found
        if (currentUser.isEmpty()){
            // Throw an exception if the user is not found
            throw new Exception("Invalid JWT signature");
        }

        // Return the current user
        return currentUser.get();
    }

    /**
     * Generates a verification code for the user with the given email.
     *
     * @param email The user's email.
     * @return The generated verification code.
     */
    public String generateVerificationCode(String email){
        // Generate a 6-digit random code
        String code = RandomStringUtils.randomNumeric(6);

        // Find the user by email
        Optional<User> userOptional = userRepository.findByEmailAddress(email);

        // Check if the user is found
        if (userOptional.isEmpty()){
            // Throw an exception if the user is not found
            throw new IllegalArgumentException("User not found");
        }

        // Get the user
        User user = userOptional.get();

        // Get the current timestamp
        Instant now = Instant.now();

        // Check if there is an existing verification code that has not expired
        if (user.getVerificationCodeTimestamp() != null){
            // Calculate the expiration time of the existing code
            Instant expirationTime = user.getVerificationCodeTimestamp().plusSeconds(300);

            // Check if the existing code has not expired
            if (now.isBefore(expirationTime)){
                // Throw an exception if the existing code has not expired
                throw new IllegalArgumentException("There is a valid code in your email");
            }
        }

        // Set the new verification code and timestamp
        user.setVerificationCode(code);
        user.setVerificationCodeTimestamp(now);

        // Save the user
        userRepository.save(user);

        // Return the generated verification code
        return code;
    }

    /**
     * Sends a verification email to the user with the given email address.
     *
     * @param email The user's email address.
     * @param code The verification code to be sent to the user.
     */
    public void sendVerificationEmail(String email, String code){
        // Find the user by email
        Optional<User> userOptional = userRepository.findByEmailAddress(email);

        // Check if the user is found
        if (userOptional.isEmpty()){
            // Throw an exception if the user is not found
            throw new IllegalArgumentException("User not found");
        }

        // Get the user
        User user = userOptional.get();

        // Check if the user's account is already verified
        if (user.isActive()){
            // Throw an exception if the user's account is already verified
            throw new IllegalArgumentException("User already verified");
        }

        // Create a simple mail message
        SimpleMailMessage message = new SimpleMailMessage();

        // Set the recipient's email address
        message.setTo(email);

        // Set the email subject
        message.setSubject("FinanceApp - Código de Verificación");

        // Set the email body with the verification code
        message.setText("Hola " + user.getName() + ",\n\nTu código de verificación es: " + code +
                "\n\nPor favor, ingresa este código en la aplicación para continuar con tu registro." +
                "\nRecuerda que este código es personal y no debes compartirlo con nadie." +
                "\n\nGracias por elegir FinanceApp.");


        // Send the email using the JavaMailSender
        javaMailSender.send(message);
    }

    /**
     * Verifies the verification code for the user with the given email address.
     *
     * @param email The user's email address.
     * @param code The verification code to be verified.
     * @return True if the code is valid, false otherwise.
     */
    public boolean verifyCode(String email, String code){
        // Find the user by email
        Optional<User> userOptional = userRepository.findByEmailAddress(email);

        // Check if the user is found
        if (userOptional.isEmpty()){
            // Throw an exception if the user is not found
            throw new IllegalArgumentException("User not found");
        }

        // Get the user
        User user = userOptional.get();

        // Get the current timestamp
        Instant now = Instant.now();

        // Calculate the expiration time of the verification code
        Instant expirationTime = user.getVerificationCodeTimestamp().plusSeconds(300); // 5 minutes

        // Check if the verification code has expired
        if (now.isAfter(expirationTime)){
            // Throw an exception if the verification code has expired
            throw new IllegalArgumentException("Code is expired");
        }

        // Verify if the verification code matches the one stored in the database
        if (!user.getVerificationCode().equals(code)){
            // Throw an exception if the verification code does not match
            throw new IllegalArgumentException("Invalid code");
        }

        // Return true if the verification code is valid
        return true;
    }

    /**
     * Activates the user's account with the given email address.
     *
     * @param email The user's email address.
     */
    public void activateAccount(String email){
        // Find the user by email
        Optional<User> userOptional = userRepository.findByEmailAddress(email);

        // Check if the user is found
        if (userOptional.isEmpty()){
            // Throw an exception if the user is not found
            throw new IllegalArgumentException("User not found");
        }

        // Get the user
        User user = userOptional.get();

        // Activate the user's account by setting their status to active
        user.setActive(true);

        // Save the changes to the database
        userRepository.save(user);
    }

}
