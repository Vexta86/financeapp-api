package com.api.financeapp.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Entity
public class UserOTP {
    @EmbeddedId
    private UserOTPId id;

    private String otp;

    private Instant otpTimestamp;

}
