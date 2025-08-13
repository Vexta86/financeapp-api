package com.api.financeapp.services;

import org.springframework.beans.factory.annotation.Value;
import com.api.financeapp.entities.*;
import com.api.financeapp.repositories.UserOTPRepository;
import com.api.financeapp.repositories.UserRepository;
import com.api.financeapp.requests.LoginRequest;
import com.api.financeapp.responses.AuthResponse;
import com.api.financeapp.security.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
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

import javax.swing.text.html.Option;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;



@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final UserRepository userRepository;
    private final UserOTPRepository otpRepository;
    private JavaMailSender javaMailSender;

    @Value("${google.client.id}")
    private String googleClientId;


    @Autowired
    public AuthService(UserRepository repo, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, UserService userService, UserRepository userRepository, UserOTPRepository otpRepository, JavaMailSender javaMailSender) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
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
        return new AuthResponse(token);
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
    public String generateVerificationCode(String email, OTPType type){

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

        // Create the OTP ID related to the user
        UserOTPId userOTPId = new UserOTPId(user, type);

        // Finds the OTP in the database
        Optional<UserOTP> userOTPOptional = otpRepository.findById(userOTPId);

        // Check if the user's account is already verified
        if (user.isActive() && type.equals(OTPType.EMAIL_VALIDATION)){
            // Throw an exception if the user's account is already verified
            throw new IllegalArgumentException("User already verified");
        }
        // Get the current timestamp
        Instant now = Instant.now();
        // Check if an OTP already exists for the given UserOTPId
        if (userOTPOptional.isPresent()) {
            // Retrieve the existing UserOTP entity
            UserOTP otp = userOTPOptional.get();



            // Check if the existing OTP was sent less than 60 seconds ago
            if (now.isBefore(otp.getOtpTimestamp().plusSeconds(60))) {
                // If the time limit has not passed, throw an exception to enforce the 1-minute rule
                throw new IllegalArgumentException("Wait 1 minute until we can send another OTP");
            }

            // Update the existing OTP with a new code and timestamp
            otp.setOtp(code);
            otp.setOtpTimestamp(now);

            otpRepository.save(otp);

            return otp.getOtp();
        } else {
            // If no existing OTP is found, create a new UserOTP instance
            UserOTP newOtp = new UserOTP();
            newOtp.setId(userOTPId);
            newOtp.setOtp(code);
            newOtp.setOtpTimestamp(now);

            // Save the new OTP entity to the database
            otpRepository.save(newOtp);

            return newOtp.getOtp();
        }
        // Return the generated verification code

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

        // Create a simple mail message
        SimpleMailMessage message = getSimpleMailMessage(email, code, user);


        // Send the email using the JavaMailSender
        javaMailSender.send(message);
    }

    /**
     * Creates a SimpleMailMessage object with the verification code email details.
     *
     * @param email User's email address
     * @param code  Verification code
     * @param user  User object containing the user's name
     * @return A SimpleMailMessage object ready to be sent
     */
    private static SimpleMailMessage getSimpleMailMessage(String email, String code, User user) {
        SimpleMailMessage message = new SimpleMailMessage();

        // Set the recipient's email address
        message.setFrom("noresponse@intfinity.co");
        message.setTo(email);

        // Set the email subject
        message.setSubject("Intfinity Account - Código de Verificación");

        // Set the email body with the verification code
        message.setText("Hola " + user.getName() + ",\n\nTu código de verificación es: " + code +
                "\n\nPor favor, ingresa este código en la aplicación para verificar tu identidad." +
                "\nRecuerda que este código es personal y no debes compartirlo con nadie." +
                "\n\nGracias por usar Intfinity.");
        return message;
    }

    /**
     * Retrieves an Optional UserOTP entity associated with the given email address.
     *
     * @param email Email address to search for
     * @return An Optional UserOTP entity, or an empty Optional if not found
     */
    private Optional<UserOTP> getOptionalOTP(String email, OTPType type){
        // Find the user by email
        Optional<User> userOptional = userRepository.findByEmailAddress(email);

        // Check if the user is found
        if (userOptional.isEmpty()){
            // Throw an exception if the user is not found
            throw new IllegalArgumentException("User not found");
        }

        // Get the user
        User user = userOptional.get();

        // Create the OTP ID related to the user
        UserOTPId userOTPId = new UserOTPId(user, type);

        // Finds the OTP in the database
        return otpRepository.findById(userOTPId);
    }
    /**
     * Verifies the verification code for the user with the given email address.
     *
     * @param email The user's email address.
     * @param code The verification code to be verified.
     * @return True if the code is valid, false otherwise.
     */
    public boolean verifyCode(String email, String code, OTPType type){
        Optional<UserOTP> userOTPOptional = getOptionalOTP(email, type);

        // Check if the verification code exists
        if (userOTPOptional.isEmpty()){
            // Throw an exception if the verification code hasn't been created
            throw new IllegalArgumentException("Code is not valid");
        }
        UserOTP otp = userOTPOptional.get();
        // Get the current timestamp
        Instant now = Instant.now();

        // Calculate the expiration time of the verification code
        Instant expirationTime = otp.getOtpTimestamp().plusSeconds(300); // 5 minutes

        // Check if the verification code has expired
        if (now.isAfter(expirationTime)){

            // Throw an exception if the verification code has expired
            throw new IllegalArgumentException("Code is expired");
        }

        // Verify if the verification code matches the one stored in the database
        if (!otp.getOtp().equals(code)){

            // Throw an exception if the verification code does not match
            throw new IllegalArgumentException("Invalid code");
        }

        // Return true if the verification code is valid
        return true;
    }
    public void changePassword(String email, String password){
        Optional<User> userOptional = userRepository.findByEmailAddress(email);
        if (userOptional.isEmpty()){
            throw new IllegalArgumentException("User not found");
        }
        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
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

    public AuthResponse googleLogin(String tokenId) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), new GsonFactory()
        ).setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(tokenId);
        if (idToken == null) {
            throw  new IllegalArgumentException("Invalid token");
        }
        GoogleIdToken.Payload payload = idToken.getPayload();

        // Check if Google verifies the email
        if (!Boolean.TRUE.equals(payload.getEmailVerified())) {
            throw new IllegalArgumentException("Email not verified");
        }

        String email = payload.getEmail();

        Optional<User> user = repo.findByEmailAddress(payload.getEmail());
        if (user.isEmpty()) {
            User newUser = new User();
            newUser.setEmailAddress(email);
            newUser.setName((String) payload.get("given_name"));
            newUser.setLastName((String) payload.get("family_name"));
            newUser.setActive(true); // Automatically activate the account
            newUser.setRole(Role.USER); // Set default role
            repo.save(newUser);
            user = Optional.of(newUser);
        }
        String jwt = jwtService.generateToken(user.get());

        return new AuthResponse(jwt);
    }
}
