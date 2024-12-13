package com.api.financeapp.controllers;


import com.api.financeapp.entities.OTPType;
import com.api.financeapp.entities.User;
import com.api.financeapp.requests.ChangePasswordRequest;
import com.api.financeapp.requests.GetCodeRequest;
import com.api.financeapp.requests.LoginRequest;
import com.api.financeapp.requests.VerifyCodeRequest;
import com.api.financeapp.responses.AuthResponse;
import com.api.financeapp.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(service.login(request));
    }
    @PostMapping("/register")
    public ResponseEntity<Object > register(@RequestBody User request){
        User registeredUser = service.register(request);
        // Generate and send a verification code
        String code = service.generateVerificationCode(registeredUser.getEmailAddress(), OTPType.EMAIL_VALIDATION);
        service.sendVerificationEmail(request.getEmailAddress(), code);
        return ResponseEntity.ok("Verification code sent. Please check your inbox.");
    }
    @PostMapping("/send-code")
    public ResponseEntity<Object> emailVerificationCode(
            @RequestBody GetCodeRequest request,
            @RequestParam OTPType type
    ){
        String code = service.generateVerificationCode(request.getEmailAddress(), type);
        service.sendVerificationEmail(request.getEmailAddress(), code);
        return ResponseEntity.ok("Verification code sent. Please check your inbox.");

    }
    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(@RequestBody VerifyCodeRequest request){
        // Verify the code
        if(service.verifyCode(request.getEmailAddress(), request.getCode(), OTPType.EMAIL_VALIDATION)){
            service.activateAccount(request.getEmailAddress());
            return ResponseEntity.ok("Email verified successfully. You can now log in.");
        } else {
            return ResponseEntity.badRequest().body("Invalid verification code.");
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<Object> changePassword(@RequestBody ChangePasswordRequest request){
        if (service.verifyCode(request.getEmailAddress(), request.getCode(), OTPType.PASSWORD_CHANGE)){
            service.changePassword(request.getEmailAddress(), request.getNewPassword());
            return ResponseEntity.ok("Password changed successfully. You can now log in.");
        }
        else {
            return ResponseEntity.badRequest().body("Invalid verification code.");
        }
    }

}

